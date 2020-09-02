import React, { ChangeEvent, FC } from 'react';
import { Directory } from '@types';
import Card from 'react-bootstrap/Card';
import { useTranslation } from 'react-i18next';
import FormCheck from 'react-bootstrap/FormCheck';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import Button from 'react-bootstrap/Button';
import { Trash } from 'react-feather';
import { useDispatch } from '@hooks';
import { unregisterDirectory, updateDirectory } from '@reducers';

interface DirectoryItemProps {
  directory: Directory;
}

export const DirectoryItem: FC<DirectoryItemProps> = ({ directory }) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const setAutomaticScanEnabled = (e: ChangeEvent<HTMLInputElement>) => {
    dispatch(
      updateDirectory({
        directory: directory.copy({
          automaticScanEnabled: e.target.checked,
        }),
      })
    );
  };

  const onUnregisterDirectory = () => {
    if (window.confirm(t('directories.directory_item.confirm_unregister', directory))) {
      dispatch(unregisterDirectory({ directoryId: directory.id }));
    }
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
          <Button variant="danger" size="sm" onClick={onUnregisterDirectory}>
            <Trash />
          </Button>
        </ButtonGroup>
      </Card.Footer>
    </Card>
  );
};
