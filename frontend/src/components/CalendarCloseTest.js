import React, { useState } from 'react';
import { DatePicker, message } from 'antd';

const { RangePicker } = DatePicker;

const CalendarCloseTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    
    if (dates && dates.length === 2) {
      message.success(`Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
    }
  };

  const handleOpenChange = (open) => {
    console.log('Picker open state:', open);
    setPickerOpen(open);
  };

  return (
    <div style={{ 
      padding: '20px', 
      border: '2px solid #52c41a', 
      margin: '20px', 
      background: '#fff',
      borderRadius: '8px'
    }}>
      <h2 style={{ color: '#52c41a', marginBottom: '20px' }}>
        ✅ Calendar Close Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Controlled Range Picker:</h3>
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
          onOpenChange={handleOpenChange}
          getPopupContainer={() => document.body}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h4>Status:</h4>
        <p>Picker is <strong style={{ color: pickerOpen ? '#ff4d4f' : '#52c41a' }}>
          {pickerOpen ? 'OPEN' : 'CLOSED'}
        </strong></p>
        <p>Selected Range: {selectedRange && selectedRange.length === 2 
          ? `${selectedRange[0].format('YYYY-MM-DD HH:mm')} to ${selectedRange[1].format('YYYY-MM-DD HH:mm')}`
          : 'None'}
        </p>
      </div>

      <div style={{ 
        background: '#f6ffed', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #b7eb8f'
      }}>
        <h4>Test Instructions:</h4>
        <ol>
          <li>Click on the date input field above</li>
          <li>Select start and end dates/times</li>
          <li>Calendar should auto-close after 1 second</li>
          <li>OR click the OK button to close immediately</li>
          <li>Check that the picker status shows "CLOSED"</li>
        </ol>
      </div>

      <div style={{ 
        background: '#fff7e6', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #ffd591',
        marginTop: '15px'
      }}>
        <h4>Expected Behavior:</h4>
        <ul>
          <li>✅ Calendar opens when clicking input</li>
          <li>✅ Calendar auto-closes after selecting both dates</li>
          <li>✅ OK button closes calendar immediately</li>
          <li>✅ Status updates correctly</li>
        </ul>
      </div>
    </div>
  );
};

export default CalendarCloseTest;
