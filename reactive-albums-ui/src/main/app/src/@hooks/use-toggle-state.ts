import { useCallback, useState } from 'react';

export function useToggleState(initial = false): [boolean, (set: boolean) => void, () => void, () => void] {
  const [state, setState] = useState(initial);
  const setTrueCallback = useCallback(() => setState(true), [setState]);
  const setFalseCallback = useCallback(() => setState(false), [setState]);

  return [state, setState, setTrueCallback, setFalseCallback];
}
