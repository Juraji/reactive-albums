import React, { FC } from 'react';
import { NotificationEffects } from './notification-effects';
import { PicturesStateLoaderEffects } from './pictures-state-loader-effects';
import { DirectoriesStateLoaderEffects } from './directories-state-loader-effects';

export const GlobalEffectors: FC = () => (
  <>
    <NotificationEffects />
    <PicturesStateLoaderEffects />
    <DirectoriesStateLoaderEffects />
  </>
);
