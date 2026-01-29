import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import './MetricsChart.css';

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend
);

function MetricsChart({ metrics }) {
  if (!metrics || metrics.length === 0) {
    return null;
  }

  // Prepare data for the chart
  const labels = metrics.map(m => m.name);
  const values = metrics.map(m => m.value);
  const minValues = metrics.map(m => m.minNormal || 0);
  const maxValues = metrics.map(m => m.maxNormal || 0);
  
  // Color code based on status
  const backgroundColors = metrics.map(m => {
    switch (m.status) {
      case 'critical': return 'rgba(220, 38, 38, 0.8)'; // Red
      case 'high': return 'rgba(251, 146, 60, 0.8)'; // Orange
      case 'low': return 'rgba(251, 146, 60, 0.8)'; // Orange
      case 'normal': return 'rgba(34, 197, 94, 0.8)'; // Green
      default: return 'rgba(156, 163, 175, 0.8)'; // Gray
    }
  });
  
  const borderColors = metrics.map(m => {
    switch (m.status) {
      case 'critical': return 'rgba(220, 38, 38, 1)';
      case 'high': return 'rgba(251, 146, 60, 1)';
      case 'low': return 'rgba(251, 146, 60, 1)';
      case 'normal': return 'rgba(34, 197, 94, 1)';
      default: return 'rgba(156, 163, 175, 1)';
    }
  });

  const data = {
    labels,
    datasets: [
      {
        label: 'Your Value',
        data: values,
        backgroundColor: backgroundColors,
        borderColor: borderColors,
        borderWidth: 2,
      },
      {
        label: 'Min Normal',
        data: minValues,
        backgroundColor: 'rgba(59, 130, 246, 0.3)',
        borderColor: 'rgba(59, 130, 246, 0.8)',
        borderWidth: 1,
        type: 'line',
        pointRadius: 0,
        borderDash: [5, 5],
      },
      {
        label: 'Max Normal',
        data: maxValues,
        backgroundColor: 'rgba(59, 130, 246, 0.3)',
        borderColor: 'rgba(59, 130, 246, 0.8)',
        borderWidth: 1,
        type: 'line',
        pointRadius: 0,
        borderDash: [5, 5],
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          font: {
            size: 12,
            family: "'Inter', sans-serif"
          },
          padding: 15,
          usePointStyle: true,
        }
      },
      title: {
        display: true,
        text: 'Blood Test Metrics Visualization',
        font: {
          size: 18,
          weight: 'bold',
          family: "'Inter', sans-serif"
        },
        padding: {
          top: 10,
          bottom: 20
        }
      },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        padding: 12,
        titleFont: {
          size: 14,
          weight: 'bold'
        },
        bodyFont: {
          size: 13
        },
        callbacks: {
          label: function(context) {
            const metric = metrics[context.dataIndex];
            if (context.dataset.label === 'Your Value') {
              return `${context.dataset.label}: ${context.parsed.y} ${metric.unit} (${metric.status})`;
            }
            return `${context.dataset.label}: ${context.parsed.y} ${metric.unit}`;
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.05)'
        },
        ticks: {
          font: {
            size: 11
          }
        }
      },
      x: {
        grid: {
          display: false
        },
        ticks: {
          font: {
            size: 11
          },
          maxRotation: 45,
          minRotation: 45
        }
      }
    }
  };

  return (
    <div className="metrics-chart-container">
      <div className="chart-wrapper">
        <Bar data={data} options={options} />
      </div>
      <div className="metrics-legend">
        <div className="legend-item">
          <span className="legend-color normal"></span>
          <span>Normal Range</span>
        </div>
        <div className="legend-item">
          <span className="legend-color warning"></span>
          <span>Out of Range</span>
        </div>
        <div className="legend-item">
          <span className="legend-color critical"></span>
          <span>Critical</span>
        </div>
      </div>
    </div>
  );
}

export default MetricsChart;
