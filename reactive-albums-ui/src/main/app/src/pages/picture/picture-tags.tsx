import React, { FC, useMemo } from 'react';
import { Picture, Tag, TagType } from '@types';
import Card from 'react-bootstrap/Card';
import { Conditional, PictureTag } from '@components';
import { Plus } from 'react-feather';
import { linkPictureTag, setPictureOverviewFilter, unlinkPictureTag, useTags } from '@reducers';
import Dropdown from 'react-bootstrap/Dropdown';
import { useDispatch } from '@hooks';
import { unwrapResult } from '@reduxjs/toolkit';
import { useTranslation } from 'react-i18next';
import { useToasts } from 'react-toast-notifications';
import { useHistory } from 'react-router-dom';

interface AddTagButtonProps {
  pictureId: string;
}

const AddTagButton: FC<AddTagButtonProps> = ({ pictureId }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();
  const tags = useTags();

  function onTagSelected(tagId: string) {
    dispatch(linkPictureTag({ pictureId, tagId }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.tags.add_tag_success'), { appearance: 'success' }))
      .catch((e) => addToast(t('picture.tags.add_tag_failed', e), { appearance: 'error' }));
  }

  return (
    <>
      <Dropdown onSelect={onTagSelected}>
        <Dropdown.Toggle id="add-tag-dropdown" size="sm">
          <Plus />
        </Dropdown.Toggle>

        <Dropdown.Menu>
          {tags.map((tag) => (
            <Dropdown.Item key={tag.id} eventKey={tag.id}>
              <PictureTag tag={tag} />
            </Dropdown.Item>
          ))}
        </Dropdown.Menu>
      </Dropdown>
    </>
  );
};

interface PictureTagsProps {
  picture: Picture;
}

export const PictureTags: FC<PictureTagsProps> = ({ picture }) => {
  const { t } = useTranslation();
  const { addToast } = useToasts();
  const dispatch = useDispatch();
  const history = useHistory();

  const directoryTags = useMemo(() => picture.tags.filter((t) => t.tagType === TagType.DIRECTORY), [picture]);
  const otherTags = useMemo(() => picture.tags.filter((t) => t.tagType !== TagType.DIRECTORY), [picture]);

  function onTagClicked(tag: Tag) {
    return () => {
      dispatch(setPictureOverviewFilter(`tag:${tag.label}`));
      history.push('/home');
    };
  }

  function onPictureTagUnlink(tagId: string) {
    dispatch(unlinkPictureTag({ pictureId: picture.id, tagId }))
      .then(unwrapResult)
      .then(() => addToast(t('picture.tags.unlink_tag_success'), { appearance: 'success' }))
      .catch((e) => addToast(t('picture.tags.unlink_tag_failed', e), { appearance: 'error' }));
  }

  return (
    <Card className="mb-2">
      <Card.Body>
        <Conditional condition={directoryTags.isNotEmpty()}>
          <ul className="list-unstyled">
            {directoryTags.map((tag, idx) => (
              <li key={idx}>
                <PictureTag tag={tag} onClick={onTagClicked(tag)} />
              </li>
            ))}
          </ul>
        </Conditional>
        <Conditional condition={otherTags.isNotEmpty()}>
          <ul className="list-unstyled">
            {otherTags.map((tag, idx) => (
              <li key={idx}>
                <PictureTag tag={tag} onClick={onTagClicked(tag)} />
                <button
                  type="button"
                  className="close onclick-btn float-none mr-3"
                  onClick={() => onPictureTagUnlink(tag.id)}
                >
                  <span aria-hidden="true">×</span>
                  <span className="sr-only">Close</span>
                </button>
              </li>
            ))}
          </ul>
        </Conditional>
      </Card.Body>
      <Card.Footer className="d-flex flex-row-reverse">
        <AddTagButton pictureId={picture.id} />
      </Card.Footer>
    </Card>
  );
};
