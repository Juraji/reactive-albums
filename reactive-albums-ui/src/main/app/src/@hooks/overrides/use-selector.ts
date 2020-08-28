import { TypedUseSelectorHook, useSelector as reactUseSelector } from 'react-redux';
import { AppState } from '@reducers';

export const useSelector: TypedUseSelectorHook<AppState> = reactUseSelector;
