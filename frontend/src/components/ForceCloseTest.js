import React, { useState, useRef } from 'react';
import { DatePicker, message } from 'antd';

const { RangePicker } = DatePicker;

const ForceCloseTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);
  const [pickerOpen, setPickerOpen] = useState(false);
  const pickerRef = useRef(null);

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    
    if (dates && dates.length === 2) {
      message.success(`✅ Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
      
      // Force close after both dates are selected
      setTimeout(() => {
        console.log('Force closing picker');
        setPickerOpen(false);
      }, 200);
    } else if (dates === null) {
      message.info('Selection cleared');
    }
  };

  const handleOpenChange = (open) => {
    console.log('Picker open state:', open);
    setPickerOpen(open);
  };

  return (
    <div style={{ 
      padding: '20px', 
      border: '2px solid #f5222d', 
      margin: '20px', 
      background: '#fff',
      borderRadius: '8px'
    }}>
      <h2 style={{ color: '#f5222d', marginBottom: '20px' }}>
        🔥 Force Close Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Controlled Range Picker with Force Close:</h3>
        <RangePicker
          ref={pickerRef}
          showTime={{
            format: 'HH:mm',
            minuteStep: 15,
            use12Hours: false
          }}
          format="YYYY-MM-DD HH:mm"
          placeholder={['Start Date & Time', 'End Date & Time']}
          style={{ width: '100%', height: '40px' }}
          value={selectedRange}
          open={pickerOpen}
          onChange={handleRangeChange}
          onOpenChange={handleOpenChange}
          getPopupContainer={() => document.body}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h4>Status:</h4>
        <p>Picker is <strong style={{ color: pickerOpen ? '#f5222d' : '#52c41a' }}>
          {pickerOpen ? 'OPEN' : 'CLOSED'}
        </strong></p>
        <p>Selected Range: {selectedRange && selectedRange.length === 2 
          ? `${selectedRange[0].format('YYYY-MM-DD HH:mm')} to ${selectedRange[1].format('YYYY-MM-DD HH:mm')}`
          : 'None'}
        </p>
      </div>

      <div style={{ 
        background: '#fff1f0', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #ffccc7'
      }}>
        <h4>Instructions:</h4>
        <ol>
          <li>Click on the date input field above</li>
          <li>Select start date and time</li>
          <li>Select end date and time</li>
          <li>Calendar should automatically close after 200ms</li>
          <li>Check that status shows "CLOSED"</li>
          <li>Both dates should remain visible</li>
        </ol>
      </div>

      <div style={{ 
        background: '#f6ffed', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #b7eb8f',
        marginTop: '15px'
      }}>
        <h4>Expected Behavior:</h4>
        <ul>
          <li>✅ Calendar opens when clicking input</li>
          <li>✅ No continuous toggling between open/closed</li>
          <li>✅ Start date is saved when selected</li>
          <li>✅ End date is saved when selected</li>
          <li>✅ Calendar auto-closes after both dates selected</li>
          <li>✅ Both dates remain visible after selection</li>
        </ul>
      </div>
    </div>
  );
};

export default ForceCloseTest;
