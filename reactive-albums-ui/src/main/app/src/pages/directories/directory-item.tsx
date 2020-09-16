import React, { ChangeEvent, FC, useState } from 'react';
import { Directory } from '@types';
import Card from 'react-bootstrap/Card';
import { useTranslation } from 'react-i18next';
import FormCheck from 'react-bootstrap/FormCheck';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Trash } from 'react-feather';
import { useDispatch, useToggleState } from '@hooks';
import { unregisterDirectory, updateDirectory } from '@reducers';
import Modal from 'react-bootstrap/Modal';
import { unwrapResult } from '@reduxjs/toolkit';

interface DeleteDirectoryConfirmProps {
  directory: Directory;
}

const DeleteDirectoryButton: FC<DeleteDirectoryConfirmProps> = ({ directory }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const [show, handleShow, handleClose] = useToggleState(false);
  const [recursive, setRecursive] = useState(false);

  const onUnregisterDirectory = () => {
    dispatch(unregisterDirectory({ directoryId: directory.id, recursive }))
      .then(unwrapResult)
      .then(handleClose);
  };

  return (
    <>
      <Button variant="danger" size="sm" onClick={handleShow}>
        <Trash />
      </Button>

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{t('directories.directory_item.confirm_unregister.modal_title', directory)}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>{t('directories.directory_item.confirm_unregister.message', directory)}</p>
          <FormCheck
            label={t('directories.directory_item.confirm_unregister.recursive_label')}
            checked={recursive}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setRecursive(e.target.checked)}
          />
        </Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={onUnregisterDirectory}>
            {t('directories.directory_item.confirm_unregister.confirm_button')}
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

interface DirectoryItemProps {
  directory: Directory;
}

export const DirectoryItem: FC<DirectoryItemProps> = ({ directory }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const setAutomaticScanEnabled = (e: ChangeEvent<HTMLInputElement>) => {
    dispatch(
      updateDirectory({
        directoryId: directory.id,
        automaticScanEnabled: e.target.checked,
      })
    );
  };

  return (
    <Card className="mb-2">
      <Card.Header>{directory.displayName}</Card.Header>
      <Card.Body>
        <ul className="list-unstyled m-0">
          <li>
            <span>{directory.location}</span>
          </li>
          <li>
            <FormCheck
              checked={directory.automaticScanEnabled}
              onChange={setAutomaticScanEnabled}
              label={t('directories.directory_item.info.automatic_scan_enabled')}
            />
          </li>
        </ul>
      </Card.Body>
      <Card.Footer>
        <ButtonGroup>
          <DeleteDirectoryButton directory={directory} />
        </ButtonGroup>
      </Card.Footer>
    </Card>
  );
};
