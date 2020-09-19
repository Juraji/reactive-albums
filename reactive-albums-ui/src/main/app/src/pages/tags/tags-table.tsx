import React, { FC } from 'react';
import Table from 'react-bootstrap/Table';
import { PictureTag } from '@components';
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import { Search } from 'react-feather';
import { EditTagButton } from './edit-tag-button';
import { useTags } from '@reducers';
import { DeleteTagButton } from './delete-tag-button';

interface TagsTableProps {}

export const TagsTable: FC<TagsTableProps> = () => {
  const tags = useTags();

  return (
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
  );
};
