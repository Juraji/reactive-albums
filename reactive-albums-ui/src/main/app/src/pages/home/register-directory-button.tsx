import React, { FC, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import { AlertTriangle, CheckCircle, Plus, Repeat } from 'react-feather';

import './register-directory-button.scss';
import { useDispatch } from '@hooks';
import { Formik, FormikProps } from 'formik';
import {
  registerDirectoryButtonFormSchema,
  RegisterDirectoryForm,
  registerDirectoryFormInitialValues,
} from './register-directory-button-form-schema';
import Form from 'react-bootstrap/Form';
import { append, formikControlProps, formikIsFormValid, merge, replace, update } from '@utils';
import { registerDirectory } from '@reducers';
import { unwrapResult } from '@reduxjs/toolkit';

interface RegisterDirectoryButtonProps {}

interface UploadHistoryItem extends RegisterDirectoryForm {
  status: 'busy' | 'completed' | 'failed';
  message?: string;
}

export const RegisterDirectoryButton: FC<RegisterDirectoryButtonProps> = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const [show, setShow] = useState(false);
  const [uploadHistory, setUploadHistory] = useState<UploadHistoryItem[]>([]);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const onSubmitForm = (e: RegisterDirectoryForm) => {
    console.log(e);
    const historyItem: UploadHistoryItem = { ...e, status: 'busy' };
    setUploadHistory(append(uploadHistory, historyItem));

    dispatch(registerDirectory(e))
      .then(unwrapResult)
      .then(() => {
        let index = uploadHistory.findIndex((h) => h.location === e.location);
        setUploadHistory(replace(uploadHistory, index, update(uploadHistory[index], 'status', 'completed')));
      })
      .catch((e) => {
        let index = uploadHistory.findIndex((h) => h.location === e.location);
        setUploadHistory(
          replace(
            uploadHistory,
            index,
            merge(uploadHistory[index], {
              status: 'failed',
              message: e.message,
            })
          )
        );
      });
  };

  return (
    <>
      <Button variant="primary" className="btn-rounded shadow-lg add-pictures-btn" onClick={handleShow}>
        <Plus />
      </Button>

      <Modal show={show} onHide={handleClose} dialogClassName="add-pictures-modal">
        <Formik
          initialValues={registerDirectoryFormInitialValues}
          onSubmit={onSubmitForm}
          validationSchema={registerDirectoryButtonFormSchema}
        >
          {(formikBag: FormikProps<RegisterDirectoryForm>) => (
            <Form noValidate onSubmit={formikBag.handleSubmit}>
              <Modal.Header closeButton>
                <Modal.Title>{t('home.add-pictures-modal.title')}</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <Form.Group>
                  <Form.Label>{t('home.add-pictures-modal.form.field_location.label')}</Form.Label>
                  <Form.Control
                    placeholder={t('home.add-pictures-modal.form.field_location.placeholder')}
                    value={formikBag.values.location}
                    {...formikControlProps(formikBag, 'location')}
                  />
                </Form.Group>

                <Form.Group className="mb-2">
                  <Form.Check
                    custom
                    type="checkbox"
                    id="register-directory-recursive"
                    label={t('home.add-pictures-modal.form.field_recursive.label')}
                    checked={formikBag.values.recursive}
                    {...formikControlProps(formikBag, 'recursive')}
                  />
                </Form.Group>
                <ul className="list-unstyled upload-history">
                  {uploadHistory.map((item, index) => (
                    <li key={index} className={`upload-history-item ${item.status}`}>
                      <span>{item.location}</span>
                      {item.recursive ? <Repeat className="ml-2" /> : null}
                      {item.status === 'busy' ? <Repeat className="ml-2" /> : null}
                      {item.status === 'completed' ? <CheckCircle className="ml-2" /> : null}
                      {item.status === 'failed' ? <AlertTriangle className="ml-2" /> : null}
                    </li>
                  ))}
                </ul>
              </Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                  {t('home.add-pictures-modal.close-btn')}{' '}
                </Button>
                <Button type="submit" variant="primary" disabled={!formikIsFormValid(formikBag)}>
                  {t('home.add-pictures-modal.add-btn')}{' '}
                </Button>
              </Modal.Footer>
            </Form>
          )}
        </Formik>
      </Modal>
    </>
  );
};
