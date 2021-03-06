import React, { FC, ReactNode, useCallback, useMemo } from 'react';
import { Tag, TagType } from '@types';
import Button from 'react-bootstrap/Button';
import { useDispatch, useToggleState } from '@hooks';
import { useTranslation } from 'react-i18next';
import Form from 'react-bootstrap/Form';
import { editTagFormSchema, EditTagFormValues } from './edit-tag-form-schema';
import { ColorChangeHandler, SketchPicker } from 'react-color';
import { Crosshair } from 'react-feather';
import { createTag, updateTag } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';
import { Formik, FormikProps } from 'formik';
import Modal from 'react-bootstrap/Modal';
import { formikControlProps, formikIsFormValid } from '@utils';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Conditional, PictureTag } from '@components';

interface TagColorFormGroupProps {
  label: string;
  value?: string;
  onChange: (hex: string) => void;
}

export const TagColorFormGroup: FC<TagColorFormGroupProps> = ({ label, value, onChange }) => {
  const { t } = useTranslation();
  const pickerStyle = useMemo(
    () => ({
      default: {
        picker: {
          boxShadow: 'none',
          padding: '0',
        },
      },
    }),
    []
  );

  const handleChangeComplete: ColorChangeHandler = (c) => onChange(c.hex);
  const onSetNotRandomized = () => onChange('#ffffff');

  return (
    <Form.Group>
      <Form.Label>{label}</Form.Label>
      {!value ? (
        <Button
          onClick={onSetNotRandomized}
          className="d-block"
          title={t('tags.edit_tag.form.color_picker.pick_color_tooltip')}
        >
          <Crosshair />
          &nbsp;{t('tags.edit_tag.form.color_picker.pick_color')}
        </Button>
      ) : (
        <SketchPicker styles={pickerStyle} disableAlpha onChangeComplete={handleChangeComplete} color={value} />
      )}
    </Form.Group>
  );
};

interface EditTagModalProps {
  tag: Tag | undefined;
  hideEditModal: () => void;
}

export const EditTagModal: FC<EditTagModalProps> = ({ hideEditModal, tag }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();
  const isCreate = useMemo(() => !tag?.id, [tag]);

  const modalHeader = useMemo(
    () => (isCreate ? t('tags.edit_tag.header_title.create') : t('tags.edit_tag.header_title.edit')),
    [isCreate, t]
  );

  const submitSuccess = useCallback(
    () => (isCreate ? t('tags.edit_tag.create_successful') : t('tags.edit_tag.update_successful')),
    [isCreate, t]
  );

  const submitFailed = useCallback(
    (e: Error) => {
      return isCreate ? t('tags.edit_tag.create_failed', e) : t('tags.edit_tag.update_failed', e);
    },
    [isCreate, t]
  );

  const initialValues = useMemo<EditTagFormValues>(() => ({ label: '' }.copy(tag)), [tag]);

  const onFormSubmit = (tagForm: EditTagFormValues) => {
    let p: Promise<any>;

    if (isCreate) {
      p = dispatch(createTag(tagForm)).then(unwrapResult);
    } else {
      p = dispatch(updateTag({ tag: tag!, patch: tagForm })).then(unwrapResult);
    }

    p.then(() => {
      hideEditModal();
      return addToast(submitSuccess(), { appearance: 'success' });
    }).catch((e) => addToast(submitFailed(e), { appearance: 'error' }));
  };

  return (
    <Modal show onHide={hideEditModal}>
      <Formik initialValues={initialValues} onSubmit={onFormSubmit} validationSchema={editTagFormSchema}>
        {(formikBag: FormikProps<EditTagFormValues>) => (
          <Form onSubmit={formikBag.handleSubmit}>
            <Modal.Header closeButton>
              <Modal.Title>{modalHeader}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form.Group>
                <Form.Label>{t('tags.edit_tag.form.label_field.label')}</Form.Label>
                <Form.Control
                  type="text"
                  value={formikBag.values.label || ''}
                  placeholder={t('tags.edit_tag.form.label_field.placeholder')}
                  tabIndex={1}
                  {...formikControlProps(formikBag, 'label')}
                  disabled={tag?.tagType === TagType.DIRECTORY}
                />
              </Form.Group>

              <Conditional condition={tag?.tagType !== TagType.COLOR}>
                <Row>
                  <Col sm={6}>
                    <TagColorFormGroup
                      label={t('tags.edit_tag.form.label_color_field.label')}
                      value={formikBag.values.tagColor}
                      onChange={(hex) => {
                        formikBag.setFieldValue('tagColor', hex, true);
                        formikBag.setFieldTouched('tagColor', true);
                      }}
                    />
                  </Col>
                  <Col sm={6}>
                    <TagColorFormGroup
                      label={t('tags.edit_tag.form.text_color_field.label')}
                      value={formikBag.values.textColor}
                      onChange={(hex) => {
                        formikBag.setFieldValue('textColor', hex, true);
                        formikBag.setFieldTouched('textColor', true);
                      }}
                    />
                  </Col>
                </Row>
              </Conditional>

              {!formikBag.values.label.isBlank() ? (
                <Form.Group>
                  <Form.Label className="d-block">{t('tags.edit_tag.form.preview.label')}</Form.Label>
                  <PictureTag tag={formikBag.values} fontSize="1rem" />
                </Form.Group>
              ) : null}
            </Modal.Body>
            <Modal.Footer className="d-flex flex-row">
              <span className="flex-grow-1">&nbsp;</span>
              <Button type="submit" disabled={!formikIsFormValid(formikBag)}>
                {t('tags.edit_tag.save_button_label')}
              </Button>
              <Button variant="secondary" onClick={hideEditModal}>
                {t('tags.edit_tag.cancel_button_label')}
              </Button>
            </Modal.Footer>
          </Form>
        )}
      </Formik>
    </Modal>
  );
};

interface EditTagButtonProps {
  icon: ReactNode;
  tag?: Tag;
}

export const EditTagButton: FC<EditTagButtonProps> = ({ icon, tag }) => {
  const [isShowEditModal, showEditModal, hideEditModal] = useToggleState(false);

  return (
    <>
      <Button onClick={showEditModal}>{icon}</Button>
      {isShowEditModal ? <EditTagModal tag={tag} hideEditModal={hideEditModal} /> : null}
    </>
  );
};
