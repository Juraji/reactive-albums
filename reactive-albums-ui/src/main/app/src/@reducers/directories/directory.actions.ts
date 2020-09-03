import { createAction } from '@reduxjs/toolkit';
import { Directory } from '@types';

export const upsertDirectories = createAction<Directory[]>('directories/upsertDirectories');
export const deleteDirectories = createAction<string[]>('directories/deleteDirectories');
