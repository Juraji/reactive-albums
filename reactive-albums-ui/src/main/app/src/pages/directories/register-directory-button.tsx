import React, { FC, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import { Plus } from 'react-feather';

import { useDispatch } from '@hooks';
import { Formik, FormikProps } from 'formik';
import {
  registerDirectoryButtonFormSchema,
  RegisterDirectoryForm,
  registerDirectoryFormInitialValues,
} from './register-directory-button-form-schema';
import Form from 'react-bootstrap/Form';
import { formikControlProps, formikIsFormValid } from '@utils';
import { registerDirectory } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';
import { Directory } from '@types';
import { FormikHelpers } from 'formik/dist/types';

interface DirsRegisteredToast {
  directories: Directory[];
}

const DirsRegisteredToast: FC<DirsRegisteredToast> = ({ directories }) => {
  return (
    <>
      <span>Added the following directories:</span>
      <ul>
        {directories.map((directory, index) => (
          <li key={index}>{directory.location}</li>
        ))}
      </ul>
    </>
  );
};

interface RegisterDirectoryButtonProps {}

export const RegisterDirectoryButton: FC<RegisterDirectoryButtonProps> = () => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const onSubmitForm = (e: RegisterDirectoryForm, { resetForm }: FormikHelpers<RegisterDirectoryForm>) => {
    dispatch(registerDirectory(e))
      .then(unwrapResult)
      .then((dirs: Directory[]) => {
        setShow(false);
        addToast(<DirsRegisteredToast directories={dirs} />);
        resetForm();
      })
      .catch((e) => addToast(e.message, { appearance: 'error' }));
  };

  return (
    <>
      <Button variant="primary" className="register-directory-btn" onClick={handleShow}>
        <Plus />
      </Button>

      <Modal show={show} onHide={handleClose} dialogClassName="register-directory-modal">
        <Formik
          initialValues={registerDirectoryFormInitialValues}
          onSubmit={onSubmitForm}
          validationSchema={registerDirectoryButtonFormSchema}
        >
          {(formikBag: FormikProps<RegisterDirectoryForm>) => (
            <Form noValidate onSubmit={formikBag.handleSubmit}>
              <Modal.Header closeButton>
                <Modal.Title>{t('directories.register_directories_button.modal_title')}</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <Form.Group>
                  <Form.Control
                    placeholder={t('directories.register_directories_button.form.field_location.placeholder')}
                    value={formikBag.values.location}
                    {...formikControlProps(formikBag, 'location')}
                  />
                </Form.Group>

                <Form.Group className="mb-2">
                  <Form.Check
                    custom
                    type="checkbox"
                    id="register-directory-recursive"
                    label={t('directories.register_directories_button.form.field_recursive.label')}
                    checked={formikBag.values.recursive}
                    {...formikControlProps(formikBag, 'recursive')}
                  />
                </Form.Group>
              </Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                  {t('directories.register_directories_button.close-btn')}{' '}
                </Button>
                <Button type="submit" variant="primary" disabled={!formikIsFormValid(formikBag)}>
                  {t('directories.register_directories_button.add-btn')}{' '}
                </Button>
              </Modal.Footer>
            </Form>
          )}
        </Formik>
      </Modal>
    </>
  );
};