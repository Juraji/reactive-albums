import React, { FC } from 'react';
import Table from 'react-bootstrap/Table';
import { Conditional, PictureTag } from '@components';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import { Search } from 'react-feather';
import { EditTagButton } from './edit-tag-button';
import { useTags } from '@reducers';
import { DeleteTagButton } from './delete-tag-button';
import { useTranslation } from 'react-i18next';

interface TagsTableProps {}

export const TagsTable: FC<TagsTableProps> = () => {
  const { t } = useTranslation();
  const tags = useTags();

  return (
    <Conditional condition={tags.isNotEmpty()} orElse={<p>{t('tags.no_tags_available')}</p>}>
      <Table striped className="mt-4">
        <thead />
        <tbody>
          {tags.map((tag, idx) => (
            <tr key={idx}>
              <td>
                <PictureTag tag={tag} fontSize="1rem" />
              </td>
              <td className="d-flex flex-row-reverse">
                <ButtonGroup>
                  <EditTagButton icon={<Search />} tag={tag} />
                  <DeleteTagButton tag={tag} />
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Conditional>
  );
};
