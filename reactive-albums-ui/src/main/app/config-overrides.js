const { override, addWebpackAlias, overrideDevServer } = require('customize-cra');
const path = require('path');
const pathsConfig = require('./tsconfig.paths.json');

const pathsMapping = (() => {
    const pathsMap = pathsConfig.compilerOptions.paths;
    const aliases = Object.keys(pathsConfig.compilerOptions.paths);
    return aliases.reduce((map, alias) =>
      Object.assign(map, { [alias]: path.resolve(__dirname, pathsMap[alias][0]) }), {});
})();

module.exports = {
    webpack: override(
      addWebpackAlias(pathsMapping)
    ),
    devServer: overrideDevServer(
      config => {
          config.compress = false;
          return config;
      }
    )
};
