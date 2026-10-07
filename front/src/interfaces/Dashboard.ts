import { IsoDate } from "../utils/dates";

export interface PeriodFigures {
    from: IsoDate,
    to: IsoDate,
    revenue: number,
    interactions: number,
    contactsReached: number,
}

export interface DashboardSummary {
    current: PeriodFigures,
    previous: PeriodFigures,
}

export interface TimelinePoint {
    date: IsoDate,
    revenue: number,
    interactions: number,
}
