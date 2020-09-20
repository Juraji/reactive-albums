import React, { FC } from 'react';
import Modal from 'react-bootstrap/Modal';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';

interface ConfirmModalProps {
  show: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  headerTitle?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  isValid?: boolean;
}

export const ConfirmModal: FC<ConfirmModalProps> = ({
  children,
  show,
  onConfirm,
  onCancel,
  headerTitle,
  confirmLabel,
  cancelLabel,
  isValid = true,
}) => {
  const { t } = useTranslation();

  return (
    <Modal show={show} onHide={onCancel}>
      <Modal.Header closeButton>
        <Modal.Title>{headerTitle || t('components.confirm_modal.default_header_title')}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{children}</Modal.Body>
      <Modal.Footer className="d-flex flex-row">
        <span className="flex-grow-1">&nbsp;</span>
        <Button onClick={onConfirm} disabled={!isValid}>
          {confirmLabel || t('components.confirm_modal.default_confirm_label')}
        </Button>
        <Button variant="secondary" onClick={onCancel}>
          {cancelLabel || t('components.confirm_modal.default_cancel_label')}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};
