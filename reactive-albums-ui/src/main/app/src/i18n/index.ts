import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import { isDevelopmentEnv } from '@utils';
import translation from './translation.json';

import { Iso8601DateFnsFmtModule } from './modules/iso-8601-post-processor-module';
import { RoundPostProcessorModule } from './modules/number-round-post-processor-module';
import { IfElsePostProcessorModule } from './modules/if-else-post-processor-module';
import { FileSizePostProcessorModule } from './modules/file-size.post-processor';
import { PostProcessorModuleChain } from './modules/fn-post-processor-module.abstract';

(() => {
  const postProcessorModuleChain = PostProcessorModuleChain.chainOf(
    Iso8601DateFnsFmtModule,
    RoundPostProcessorModule,
    IfElsePostProcessorModule,
    FileSizePostProcessorModule
  );

  i18next
    .use(initReactI18next)
    .use(postProcessorModuleChain)
    .init({
      lng: 'nl',
      debug: isDevelopmentEnv(),
      resources: { nl: { translation } },
      postProcess: postProcessorModuleChain.postProcessorNames(),
    });
})();
