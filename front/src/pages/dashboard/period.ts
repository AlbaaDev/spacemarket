import { addDays, startOfWeek } from '../../utils/dates';

/** A dashboard Period: both bounds are inclusive calendar days. */
export interface Period {
  from: Date;
  to: Date;
}

export type PeriodPreset = 'week' | 'month' | 'quarter';

export function presetPeriod(preset: PeriodPreset, today = new Date()): Period {
  const year = today.getFullYear();
  const month = today.getMonth();
  switch (preset) {
    case 'week': {
      const from = startOfWeek(today);
      return { from, to: addDays(from, 6) };
    }
    case 'month':
      return { from: new Date(year, month, 1), to: new Date(year, month + 1, 0) };
    case 'quarter': {
      const first = month - (month % 3);
      return { from: new Date(year, first, 1), to: new Date(year, first + 3, 0) };
    }
  }
}

export function periodDays(period: Period): number {
  return Math.round((period.to.getTime() - period.from.getTime()) / 86_400_000) + 1;
}

/** Same length, moved one whole Period backwards (-1) or forwards (+1). Calendar presets stay aligned. */
export function shiftPeriod(period: Period, direction: -1 | 1): Period {
  const preset = matchingPreset(period);
  if (preset === 'month' || preset === 'quarter') {
    const months = preset === 'month' ? 1 : 3;
    const from = new Date(period.from.getFullYear(), period.from.getMonth() + direction * months, 1);
    return { from, to: new Date(from.getFullYear(), from.getMonth() + months, 0) };
  }
  const days = periodDays(period) * direction;
  return { from: addDays(period.from, days), to: addDays(period.to, days) };
}

/** The preset this Period is exactly equal to, whatever week, month or quarter it falls in. */
export function matchingPreset(period: Period): PeriodPreset | null {
  for (const preset of ['week', 'month', 'quarter'] as const) {
    const candidate = presetPeriod(preset, period.from);
    if (sameDay(candidate.from, period.from) && sameDay(candidate.to, period.to)) {
      return preset;
    }
  }
  return null;
}

export function sameDay(a: Date, b: Date): boolean {
  return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
}
