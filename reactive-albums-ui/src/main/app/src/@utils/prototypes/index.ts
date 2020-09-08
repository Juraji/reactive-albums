import installArrayExt from './array-ext/array-ext';
import installObjExt from './object-ext/object-ext';

export default function installExtensions() {
  installArrayExt();
  installObjExt();
}
