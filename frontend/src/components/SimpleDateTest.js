import React, { useState } from 'react';
import { DatePicker, Button, message } from 'antd';

const { RangePicker } = DatePicker;

const SimpleDateTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    if (dates && dates.length === 2) {
      message.success(`Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
    }
  };

  const forceShowCalendar = () => {
    // Force show calendar programmatically
    const picker = document.querySelector('.ant-picker');
    if (picker) {
      picker.click();
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
        🗓️ Simple Date Picker Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Range Picker Test:</h3>
        <RangePicker
          showTime={{
            format: 'HH:mm',
            minuteStep: 15,
            use12Hours: false
          }}
          format="YYYY-MM-DD HH:mm"
          placeholder={['Start Date & Time', 'End Date & Time']}
          style={{ width: '100%', height: '40px' }}
          onChange={handleRangeChange}
          value={selectedRange}
          getPopupContainer={() => document.body}
          onOpenChange={(open) => {
            console.log('Calendar open state:', open);
            if (open) {
              // Force visibility
              setTimeout(() => {
                const dropdown = document.querySelector('.ant-picker-dropdown');
                if (dropdown) {
                  console.log('Forcing dropdown visibility');
                  dropdown.style.display = 'block';
                  dropdown.style.visibility = 'visible';
                  dropdown.style.opacity = '1';
                  dropdown.style.zIndex = '9999';
                  dropdown.style.position = 'fixed';
                  dropdown.style.background = '#fff';
                  dropdown.style.border = '1px solid #d9d9d9';
                  dropdown.style.borderRadius = '8px';
                  dropdown.style.boxShadow = '0 6px 16px 0 rgba(0, 0, 0, 0.08)';
                }
              }, 50);
            }
          }}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <Button onClick={forceShowCalendar} type="primary">
          Force Show Calendar
        </Button>
        <Button 
          onClick={() => {
            setSelectedRange(null);
            message.info('Cleared selection');
          }}
          style={{ marginLeft: '10px' }}
        >
          Clear Selection
        </Button>
      </div>

      <div style={{ 
        background: '#f0f8ff', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #b3d9ff'
      }}>
        <h4>Current Selection:</h4>
        {selectedRange && selectedRange.length === 2 ? (
          <div>
            <p><strong>Start:</strong> {selectedRange[0].format('YYYY-MM-DD HH:mm')}</p>
            <p><strong>End:</strong> {selectedRange[1].format('YYYY-MM-DD HH:mm')}</p>
          </div>
        ) : (
          <p>No selection made</p>
        )}
      </div>

      <div style={{ 
        background: '#fff3cd', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #ffeaa7',
        marginTop: '15px'
      }}>
        <h4>Instructions:</h4>
        <ol>
          <li>Click on the date input field above</li>
          <li>Calendar should appear immediately</li>
          <li>If calendar doesn't appear, click "Force Show Calendar" button</li>
          <li>Select start and end dates/times</li>
          <li>Check console for debug messages</li>
        </ol>
      </div>
    </div>
  );
};

export default SimpleDateTest;
