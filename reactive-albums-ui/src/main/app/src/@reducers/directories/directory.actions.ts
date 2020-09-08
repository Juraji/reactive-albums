import { createAction } from '@reduxjs/toolkit';
import { Directory } from '@types';

export const upsertEventDirectories = createAction<Directory[]>('directories/events/upsertDirectories');
export const deleteEventDirectories = createAction<string[]>('directories/events/deleteDirectories');
