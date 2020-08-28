import React, { ChangeEvent, DragEvent, FC, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import { Plus } from 'react-feather';

import './add-pictures-button.scss';
import { append } from '@utils';
import { useDispatch } from '@hooks';
import { addPicture } from '@reducers';

interface RegisterDirectoryButtonProps {}

interface FileUploadProgress {
  id: number;
  file: File;
  done: boolean;
}

export const RegisterDirectoryButton: FC<RegisterDirectoryButtonProps> = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const [show, setShow] = useState(false);
  const [dropping, setDropping] = useState(false);
  const [uploadHistory, setUploadHistory] = useState<FileUploadProgress[]>([]);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const onDragEvent = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();

    switch (e.type) {
      case 'dragover':
        setDropping(true);
        break;
      case 'dragleave':
        setDropping(false);
        break;
      case 'drop':
        setDropping(false);
        onAddFiles(e.dataTransfer.files);
        break;
    }
  };

  const onInputValueUpdated = (e: ChangeEvent<HTMLInputElement>) => {
    if (!!e.target.files) {
      onAddFiles(e.target.files);
    }
    e.target.value = '';
  };

  const onAddFiles = (fileList: FileList) => {
    const uploads: FileUploadProgress[] = Array.from(fileList).map((file) => ({
      id: Math.random(),
      file,
      done: false,
    }));

    setUploadHistory(append(uploadHistory, ...uploads));

    uploads.forEach((u) => {
      console.log(u);
      // dispatch(addPicture({ location: u.file.name }));
    });
  };

  return (
    <>
      <Button variant="primary" className="btn-rounded shadow-lg add-pictures-btn" onClick={handleShow}>
        <Plus />
      </Button>

      <Modal show={show} onHide={handleClose} dialogClassName="add-pictures-modal">
        <Modal.Header closeButton>
          <Modal.Title>Add pictures</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div
            className={`drop-zone${dropping ? ' file-drop-active' : ''}`}
            onDragOver={onDragEvent}
            onDragLeave={onDragEvent}
            onDrop={onDragEvent}
          >
            <label className="drop-zone-input-label" htmlFor="add-pictures-input">
              {dropping
                ? t('home.add-pictures-modal.drop-zone-input-label-active')
                : t('home.add-pictures-modal.drop-zone-input-label')}
            </label>
          </div>
          <input type="file" hidden id="add-pictures-input" onChange={onInputValueUpdated} multiple />
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};
