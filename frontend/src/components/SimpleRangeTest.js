import React, { useState } from 'react';
import { DatePicker, message } from 'antd';

const { RangePicker } = DatePicker;

const SimpleRangeTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    
    if (dates && dates.length === 2) {
      message.success(`✅ Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
    } else if (dates === null) {
      message.info('Selection cleared');
    }
  };

  return (
    <div style={{ 
      padding: '20px', 
      border: '2px solid #1890ff', 
      margin: '20px', 
      background: '#fff',
      borderRadius: '8px'
    }}>
      <h2 style={{ color: '#1890ff', marginBottom: '20px' }}>
        🗓️ Simple Range Picker Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Basic Range Picker (No Auto-Close):</h3>
        <RangePicker
          showTime={{
            format: 'HH:mm',
            minuteStep: 15,
            use12Hours: false
          }}
          format="YYYY-MM-DD HH:mm"
          placeholder={['Start Date & Time', 'End Date & Time']}
          style={{ width: '100%', height: '40px' }}
          value={selectedRange}
          onChange={handleRangeChange}
          getPopupContainer={() => document.body}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h4>Current Selection:</h4>
        {selectedRange && selectedRange.length === 2 ? (
          <div style={{ 
            background: '#f6ffed', 
            padding: '10px', 
            borderRadius: '5px',
            border: '1px solid #b7eb8f'
          }}>
            <p><strong>Start:</strong> {selectedRange[0].format('YYYY-MM-DD HH:mm')}</p>
            <p><strong>End:</strong> {selectedRange[1].format('YYYY-MM-DD HH:mm')}</p>
            <p><strong>Duration:</strong> {selectedRange[1].diff(selectedRange[0], 'hours', true).toFixed(1)} hours</p>
          </div>
        ) : (
          <p style={{ color: '#999' }}>No selection made</p>
        )}
      </div>

      <div style={{ 
        background: '#f0f8ff', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #b3d9ff'
      }}>
        <h4>Instructions:</h4>
        <ol>
          <li>Click on the date input field above</li>
          <li>Select start date and time</li>
          <li>Select end date and time</li>
          <li>Click OK button to confirm</li>
          <li>Check that both dates are saved and displayed</li>
        </ol>
      </div>

      <div style={{ 
        background: '#fff3cd', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #ffeaa7',
        marginTop: '15px'
      }}>
        <h4>Expected Behavior:</h4>
        <ul>
          <li>✅ Calendar opens when clicking input</li>
          <li>✅ Start date is saved when selected</li>
          <li>✅ End date is saved when selected</li>
          <li>✅ Both dates remain visible after selection</li>
          <li>✅ Calendar closes when clicking OK</li>
        </ul>
      </div>
    </div>
  );
};

export default SimpleRangeTest;
