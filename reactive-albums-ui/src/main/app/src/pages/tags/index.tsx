import React, { FC, useMemo } from 'react';
import Container from 'react-bootstrap/Container';
import { TagsTable } from './tags-table';
import { useTags } from '@reducers';
import { TagType } from '@types';
import Tabs from 'react-bootstrap/Tabs';
import Tab from 'react-bootstrap/Tab';
import { useTranslation } from 'react-i18next';
import { EditTagButton } from './edit-tag-button';
import { Plus } from 'react-feather';
import ButtonGroup from 'react-bootstrap/ButtonGroup';

const TagManagementPage: FC = () => {
  const { t } = useTranslation();
  const tags = useTags();

  const userTags = useMemo(() => tags.filter((t) => t.tagType === TagType.USER), [tags]);
  const systemTags = useMemo(() => tags.filter((t) => t.tagType !== TagType.USER), [tags]);

  return (
    <Container>
      <Tabs id="tag-management-tabs" defaultActiveKey="user-tags">
        <Tab title={t('tags.tab_user.tab_header')} eventKey="user-tags">
          <ButtonGroup className="my-2">
            <EditTagButton icon={<Plus />} />
          </ButtonGroup>
          <TagsTable tags={userTags} />
        </Tab>
        <Tab title={t('tags.tab_system.tab_header')} eventKey="system-tags">
          <TagsTable tags={systemTags} />
        </Tab>
      </Tabs>
    </Container>
  );
};

export default TagManagementPage;
