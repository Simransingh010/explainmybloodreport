import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import AnalyzerPage from '../pages/AnalyzerPage';

// Mock dependencies
vi.mock('../components/MetricsChart', () => ({
  default: () => <div data-testid="metrics-chart">Metrics Chart</div>
}));

vi.mock('react-markdown', () => ({
  default: ({ children }) => <div>{children}</div>
}));

describe('AnalyzerPage', () => {
  beforeEach(() => {
    vi.resetAllMocks();
    global.fetch = vi.fn();
    // Mock environment variable
    import.meta.env.VITE_API_URL = 'http://localhost:8080';
  });

  it('renders upload section initially', () => {
    render(
      <BrowserRouter>
        <AnalyzerPage />
      </BrowserRouter>
    );
    expect(screen.getByText(/Upload Your Blood Report/i)).toBeInTheDocument();
  });

  it('analyze button is disabled initially', () => {
    render(
      <BrowserRouter>
        <AnalyzerPage />
      </BrowserRouter>
    );
    
    const analyzeBtn = screen.getByText(/Analyze Report/i);
    expect(analyzeBtn).toBeDisabled();
  });

  it('handles successful analysis', async () => {
    const mockAnalysis = {
      riskFactors: ['Risk 1'],
      lifestyleAdvice: ['Advice 1'],
      metrics: [{ name: 'Test', value: 10, unit: 'u', status: 'normal' }],
      validationWarnings: ['Warning 1']
    };

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockAnalysis
    });

    render(
      <BrowserRouter>
        <AnalyzerPage />
      </BrowserRouter>
    );

    // Simulate file selection
    const file = new File(['test'], 'test.pdf', { type: 'application/pdf' });
    // Use the specific text found in the component
    const dropText = screen.getByText(/Drop your blood report here/i);
    // Find the input associated with the label (or nearby)
    // Since input is hidden, we can target it by ID directly for the test event
    const input = document.getElementById('file-input');
    fireEvent.change(input, { target: { files: [file] } });

    // Click analyze (now enabled)
    const analyzeBtn = screen.getByText(/Analyze Report/i);
    expect(analyzeBtn).not.toBeDisabled();
    fireEvent.click(analyzeBtn);

    // Wait for results
    await waitFor(() => {
      expect(screen.getByText(/Your Health Insights/i)).toBeInTheDocument();
      expect(screen.getByTestId('metrics-chart')).toBeInTheDocument();
      expect(screen.getByText(/Warning 1/i)).toBeInTheDocument();
    });
  });
});
