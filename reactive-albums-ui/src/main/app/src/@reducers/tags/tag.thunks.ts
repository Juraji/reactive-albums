import { Tag } from '@types';
import { createApiDeleteThunk, createApiGetThunk, createApiPostThunk, createApiPutThunk } from '@utils';

export const fetchAllTags = createApiGetThunk<Tag[]>('tags/fetchAllTags', () => '/api/tags');

interface TagThunk {
  id: string;
}

interface CreateTagThunk {
  label: string;
  tagColor?: string;
  textColor?: string;
}

export const createTag = createApiPostThunk<Tag, CreateTagThunk>(
  'tags/createTag',
  ({ label, tagColor, textColor }) => ({
    url: `/api/tags`,
    data: { label, tagColor, textColor },
  })
);

interface UpdateTagThunk {
  tag: Tag;
  patch: Partial<Tag>;
}

export const updateTag = createApiPutThunk<Tag, UpdateTagThunk>('tags/updateTag', ({ tag, patch }) => {
  const changed = (p: string | undefined, t: string) => (!!p && p !== t ? p : undefined);
  return {
    url: `/api/tags/${tag.id}`,
    data: {
      label: changed(patch.label, tag.label),
      tagColor: changed(patch.tagColor, tag.tagColor),
      textColor: changed(patch.textColor, tag.textColor),
    },
  };
});

export const deleteTag = createApiDeleteThunk<string, TagThunk>('tags/deleteTag', ({ id }) => `/api/tags/${id}`);
