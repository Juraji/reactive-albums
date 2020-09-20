import installArrayExt from './array-ext/array-ext';
import installObjExt from './object-ext/object-ext';
import installStringExt from './string-ext/string-ext';

export default function installExtensions() {
  installArrayExt();
  installObjExt();
  installStringExt();
}
