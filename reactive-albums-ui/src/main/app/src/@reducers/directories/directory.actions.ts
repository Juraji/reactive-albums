import { createAction } from '@reduxjs/toolkit';
import { Directory } from '@types';

export const upsertDirectories = createAction<Directory[]>('directories/upsertDirectories');
