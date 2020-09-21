import React, { ChangeEvent, FC, useState } from 'react';
import { Directory } from '@types';
import { useTranslation } from 'react-i18next';
import FormCheck from 'react-bootstrap/FormCheck';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Eye, EyeOff, Trash } from 'react-feather';
import { useDispatch, useToggleState } from '@hooks';
import { unregisterDirectory, updateDirectory } from '@reducers';
import Modal from 'react-bootstrap/Modal';
import { unwrapResult } from '@reduxjs/toolkit';
import { useToasts } from 'react-toast-notifications';

interface DeleteDirectoryConfirmProps {
  directory: Directory;
}

const DeleteDirectoryButton: FC<DeleteDirectoryConfirmProps> = ({ directory }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();

  const [show, handleShow, handleClose] = useToggleState(false);
  const [recursive, setRecursive] = useState(false);

  const onUnregisterDirectory = () => {
    dispatch(unregisterDirectory({ directoryId: directory.id, recursive }))
      .then(unwrapResult)
      .then(() => {
        handleClose();
        addToast(t('directories.directory_item.unregister_button.unregister_success'), { appearance: 'success' });
      })
      .catch((e) =>
        addToast(t('directories.directory_item.unregister_button.unregister_failed', e), { appearance: 'success' })
      );
  };

  return (
    <>
      <Button variant="danger" size="sm" onClick={handleShow}>
        <Trash />
      </Button>

      {show ? (
        <Modal show={show} onHide={handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>
              {t('directories.directory_item.unregister_button.confirm_unregister.modal_title', directory)}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>{t('directories.directory_item.unregister_button.confirm_unregister.message', directory)}</p>
            <FormCheck
              label={t('directories.directory_item.unregister_button.confirm_unregister.recursive_label')}
              checked={recursive}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setRecursive(e.target.checked)}
            />
          </Modal.Body>
          <Modal.Footer>
            <Button variant="danger" onClick={onUnregisterDirectory}>
              {t('directories.directory_item.unregister_button.confirm_unregister.confirm_button')}
            </Button>
          </Modal.Footer>
        </Modal>
      ) : null}
    </>
  );
};

interface DirectoryItemProps {
  directory: Directory;
}

export const DirectoryItem: FC<DirectoryItemProps> = ({ directory }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const toggleAutomaticScan = () => {
    dispatch(
      updateDirectory({
        directoryId: directory.id,
        automaticScanEnabled: !directory.automaticScanEnabled,
      })
    );
  };
  return (
    <tr>
      <td>
        {directory.location}
        <br />
        <small className="text-muted">{t('directories.directory_item.info.registered_at', directory)}</small>
      </td>
      <td className="d-flex flex-row-reverse">
        <ButtonGroup>
          <Button
            onClick={toggleAutomaticScan}
            title={t('directories.directory_item.info.automatic_scan_enabled', {
              enabled: directory.automaticScanEnabled,
            })}
          >
            {directory.automaticScanEnabled ? <Eye /> : <EyeOff />}
          </Button>
          <DeleteDirectoryButton directory={directory} />
        </ButtonGroup>
      </td>
    </tr>
  );
};
