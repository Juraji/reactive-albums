import React, { FC } from 'react';
import { NotificationEffects } from './notification-effects';
import { StateRehydrator } from './state-rehydrator';
import { ApiEventLoader } from './api-event-loader';

export const GlobalEffectors: FC = () => (
  <>
    <NotificationEffects />
    <StateRehydrator />
    <ApiEventLoader />
  </>
);
