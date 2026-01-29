import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import MetricsChart from '../components/MetricsChart';

describe('MetricsChart', () => {
  const mockMetrics = [
    {
      name: 'Glucose',
      value: 95,
      unit: 'mg/dL',
      minNormal: 70,
      maxNormal: 100,
      status: 'normal'
    },
    {
      name: 'Hemoglobin',
      value: 10.0,
      unit: 'g/dL',
      minNormal: 12.0,
      maxNormal: 16.0,
      status: 'low'
    }
  ];

  it('should render when metrics are provided', () => {
    const { container } = render(<MetricsChart metrics={mockMetrics} />);
    expect(container.querySelector('.metrics-chart-container')).toBeInTheDocument();
  });

  it('should not render when metrics array is empty', () => {
    const { container } = render(<MetricsChart metrics={[]} />);
    expect(container.querySelector('.metrics-chart-container')).not.toBeInTheDocument();
  });

  it('should not render when metrics is null', () => {
    const { container } = render(<MetricsChart metrics={null} />);
    expect(container.querySelector('.metrics-chart-container')).not.toBeInTheDocument();
  });

  it('should display legend with correct status categories', () => {
    const { container } = render(<MetricsChart metrics={mockMetrics} />);
    const legend = container.querySelector('.metrics-legend');
    expect(legend).toBeInTheDocument();
  });
});
