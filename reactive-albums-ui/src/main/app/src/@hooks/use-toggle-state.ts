import { useCallback, useState } from 'react';

type UseToggleState = [
  boolean, // Current state
  () => void, // Set true fn
  () => void, // Set false fn
  (set: boolean) => void // Direct setter
];

export function useToggleState(initial = false): UseToggleState {
  const [state, setState] = useState(initial);
  const setTrueCallback = useCallback(() => setState(true), [setState]);
  const setFalseCallback = useCallback(() => setState(false), [setState]);

  return [state, setTrueCallback, setFalseCallback, setState];
}
