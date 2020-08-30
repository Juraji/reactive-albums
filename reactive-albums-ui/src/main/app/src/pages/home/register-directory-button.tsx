import React, { FC, useEffect, useState } from 'react';
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
import { Directory } from '../../@types/Directory.domain';
import { useToasts } from 'react-toast-notifications';

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

  const onSubmitForm = (e: RegisterDirectoryForm) => {
    dispatch(registerDirectory(e))
      .then(unwrapResult)
      .then((dirs: Directory[]) => addToast(<DirsRegisteredToast directories={dirs} />))
      .catch((e) => addToast(e.message, { appearance: 'error' }));
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
