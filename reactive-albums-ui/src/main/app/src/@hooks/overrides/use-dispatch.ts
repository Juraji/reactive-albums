import { useDispatch as reactDispatchHook } from 'react-redux';
import { appStore } from '@reducers';

export type AppDispatch = typeof appStore.dispatch;
export const useDispatch: () => AppDispatch = reactDispatchHook;
