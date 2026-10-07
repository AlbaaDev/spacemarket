import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { BarChart } from 'echarts/charts';
import { BrushComponent, GridComponent, MarkAreaComponent, TitleComponent, ToolboxComponent, TooltipComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import type { EChartsCoreOption, ECharts } from 'echarts/core';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';
import { TimelinePoint } from '../../../interfaces/Dashboard';
import { ThemeService } from '../../../services/theme/theme.service';
import { addDays, fromIsoDate, startOfWeek, toIsoDate } from '../../../utils/dates';
import { Period } from '../period';
import { APP_LOCALE } from '../../../utils/date-adapter';

echarts.use([BarChart, BrushComponent, GridComponent, MarkAreaComponent, TitleComponent, ToolboxComponent, TooltipComponent, CanvasRenderer]);

const DAY = 86_400_000;

// Mark colours are validated chart steps of the teal accent (chroma and lightness band per theme);
// the rest mirrors the --mat-sys-* tokens in styles.css.
const CHART_THEME = {
  light: { mark: '#00897b', ink: '#1c2321', muted: '#4a5552', grid: '#e4e3dc', selection: 'rgba(0, 137, 123, 0.10)', tooltip: '#ffffff' },
  dark: { mark: '#22a593', ink: '#e8e9ec', muted: '#b4b8bf', grid: '#4c4e54', selection: 'rgba(34, 165, 147, 0.16)', tooltip: '#45474d' },
};

interface WeekBucket {
  start: Date;
  revenue: number;
  interactions: number;
}

/**
 * Weekly Revenue and Interactions as two stacked panels sharing one time axis.
 * The highlighted band is the Period: drag across either panel to draw a new one,
 * or drag the band and its edges to adjust it.
 */
@Component({
  selector: 'app-activity-timeline',
  imports: [NgxEchartsDirective],
  providers: [provideEchartsCore({ echarts })],
  template: `
    <div echarts class="chart" [options]="options()" [autoResize]="true" (chartInit)="onChartInit($event)"
      role="img" [attr.aria-label]="summary()"></div>
  `,
  styles: `
    :host { display: block; }
    .chart { height: 320px; width: 100%; }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityTimelineComponent {
  readonly points = input.required<TimelinePoint[]>();
  readonly extent = input.required<Period>();
  readonly period = input.required<Period>();
  readonly periodChange = output<Period>();

  private readonly theme = inject(ThemeService).theme;
  private readonly chart = signal<ECharts | null>(null);
  private readonly money = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 });
  private readonly compactMoney = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', notation: 'compact', maximumFractionDigits: 1 });
  private readonly weekLabel = new Intl.DateTimeFormat(APP_LOCALE, { day: 'numeric', month: 'short' });
  private readonly monthLabel = new Intl.DateTimeFormat(APP_LOCALE, { month: 'short' });

  readonly buckets = computed<WeekBucket[]>(() => {
    const { from, to } = this.extent();
    const byWeek = new Map<string, WeekBucket>();
    for (let start = startOfWeek(from); start <= to; start = addDays(start, 7)) {
      byWeek.set(toIsoDate(start), { start, revenue: 0, interactions: 0 });
    }
    for (const point of this.points()) {
      const bucket = byWeek.get(toIsoDate(startOfWeek(fromIsoDate(point.date))));
      if (bucket) {
        bucket.revenue += point.revenue;
        bucket.interactions += point.interactions;
      }
    }
    return [...byWeek.values()];
  });

  readonly summary = computed(() => {
    const totalRevenue = this.buckets().reduce((sum, b) => sum + b.revenue, 0);
    const totalInteractions = this.buckets().reduce((sum, b) => sum + b.interactions, 0);
    return `Weekly revenue and interactions from ${this.weekLabel.format(this.extent().from)} to ${this.weekLabel.format(this.extent().to)}: `
      + `${this.money.format(totalRevenue)} revenue, ${totalInteractions} interactions. The highlighted band is the selected period.`;
  });

  readonly options = computed<EChartsCoreOption>(() => {
    const colors = CHART_THEME[this.theme()];
    const { from, to } = this.extent();
    // Bars sit mid-week so each one visually covers its Monday-to-Sunday span.
    const x = (bucket: WeekBucket) => bucket.start.getTime() + 3.5 * DAY;
    const min = startOfWeek(from).getTime();
    const max = addDays(startOfWeek(to), 7).getTime();
    const axisCommon = {
      type: 'time', min, max,
      axisLine: { lineStyle: { color: colors.grid } },
      axisTick: { show: false },
      splitLine: { show: false },
    };
    const valueAxisCommon = {
      type: 'value',
      splitNumber: 2,
      axisLabel: { color: colors.muted, fontSize: 11 },
      splitLine: { lineStyle: { color: colors.grid } },
    };
    const barCommon = {
      type: 'bar',
      barMaxWidth: 12,
      itemStyle: { color: colors.mark, borderRadius: [4, 4, 0, 0] },
      emphasis: { disabled: true },
    };
    const titleStyle = { color: colors.ink, fontSize: 13, fontWeight: 600, fontFamily: 'Hanken Grotesk, system-ui, sans-serif' };

    return {
      animation: false,
      textStyle: { fontFamily: 'Hanken Grotesk, system-ui, sans-serif' },
      title: [
        { text: 'Revenue per week', left: 0, top: 0, textStyle: titleStyle },
        { text: 'Interactions per week', left: 0, top: 176, textStyle: titleStyle },
      ],
      grid: [
        { left: 56, right: 8, top: 32, height: 112 },
        { left: 56, right: 8, top: 208, height: 76 },
      ],
      xAxis: [
        { ...axisCommon, gridIndex: 0, axisLabel: { show: false } },
        {
          ...axisCommon, gridIndex: 1,
          axisLabel: { color: colors.muted, fontSize: 11, hideOverlap: true, formatter: (value: number) => this.monthLabel.format(new Date(value)) },
        },
      ],
      yAxis: [
        { ...valueAxisCommon, gridIndex: 0, axisLabel: { ...valueAxisCommon.axisLabel, formatter: (v: number) => this.compactMoney.format(v) } },
        { ...valueAxisCommon, gridIndex: 1, minInterval: 1 },
      ],
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'line', lineStyle: { color: colors.muted, type: 'dashed' } },
        backgroundColor: colors.tooltip,
        borderColor: colors.grid,
        textStyle: { color: colors.ink, fontSize: 12 },
        formatter: (params: { dataIndex: number }[]) => {
          const bucket = this.buckets()[params[0]?.dataIndex];
          if (!bucket) return '';
          const end = addDays(bucket.start, 6);
          return `<strong>${this.weekLabel.format(bucket.start)} – ${this.weekLabel.format(end)}</strong><br>`
            + `Revenue: ${this.money.format(bucket.revenue)}<br>Interactions: ${bucket.interactions}`;
        },
      },
      // The toolbox is hidden; brush is driven by takeGlobalCursor so dragging works without a button.
      toolbox: { show: false, feature: { brush: { type: ['lineX'] } } },
      brush: {
        xAxisIndex: 'all',
        brushLink: 'all',
        brushType: 'lineX',
        brushMode: 'single',
        transformable: true,
        removeOnClick: false,
        brushStyle: { color: colors.selection, borderColor: colors.mark, borderWidth: 1 },
        outOfBrush: { colorAlpha: 0.35 },
      },
      series: [
        { ...barCommon, name: 'Revenue', xAxisIndex: 0, yAxisIndex: 0, data: this.buckets().map(b => [x(b), b.revenue]) },
        {
          ...barCommon, name: 'Interactions', xAxisIndex: 1, yAxisIndex: 1, data: this.buckets().map(b => [x(b), b.interactions]),
          // The draggable band lives on the top panel; this mirrors it so the Period reads across both.
          markArea: {
            silent: true,
            itemStyle: { color: colors.selection, borderColor: colors.mark, borderWidth: 1 },
            data: [[{ xAxis: this.period().from.getTime() }, { xAxis: addDays(this.period().to, 1).getTime() - 1 }]],
          },
        },
      ],
    };
  });

  constructor() {
    // Redraw the band whenever the Period or the chart (theme, data) changes.
    effect(() => {
      const chart = this.chart();
      const period = this.period();
      this.options();
      if (!chart) return;
      queueMicrotask(() => this.drawBand(chart, period));
    });
  }

  onChartInit(chart: ECharts) {
    chart.on('brushEnd', (event: unknown) => {
      const area = (event as { areas?: { coordRange?: number[] }[] }).areas?.[0];
      const range = area?.coordRange;
      if (!range || range.length < 2) {
        this.drawBand(chart, this.period());
        return;
      }
      const { from: min, to: max } = this.extent();
      // Snap to whole days, inside the timeline.
      const from = new Date(Math.max(range[0], startOfWeek(min).getTime()));
      const to = new Date(Math.min(range[1], max.getTime()));
      const period = {
        from: new Date(from.getFullYear(), from.getMonth(), from.getDate()),
        to: new Date(to.getFullYear(), to.getMonth(), to.getDate()),
      };
      if (period.to < period.from) return;
      this.periodChange.emit(period);
    });
    this.chart.set(chart);
  }

  private drawBand(chart: ECharts, period: Period) {
    chart.dispatchAction({ type: 'takeGlobalCursor', key: 'brush', brushOption: { brushType: 'lineX', brushMode: 'single' } });
    chart.dispatchAction({
      type: 'brush',
      areas: [{
        brushType: 'lineX',
        xAxisIndex: 0,
        // The band covers whole days: from the first day's start to the last day's end.
        coordRange: [period.from.getTime(), addDays(period.to, 1).getTime() - 1],
      }],
    });
  }
}
