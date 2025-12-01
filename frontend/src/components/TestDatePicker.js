import React, { useState } from 'react';
import { DatePicker, Button, message } from 'antd';
import moment from 'moment';

const { RangePicker } = DatePicker;

const TestDatePicker = () => {
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedRange, setSelectedRange] = useState(null);

  const disabledDate = (current) => {
    return current && current < moment().startOf('day');
  };

  const handleDateChange = (date) => {
    console.log('Date changed:', date);
    setSelectedDate(date);
    message.success(`Selected date: ${date ? date.format('YYYY-MM-DD') : 'None'}`);
  };

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    if (dates && dates.length === 2) {
      message.success(`Selected range: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
    }
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #ccc', margin: '20px', background: '#fff' }}>
      <h3>Date Picker Test Page</h3>
      <p>Click on the inputs below to test if calendars appear:</p>
      
      {/* Simple DatePicker test */}
      <div style={{ marginBottom: '20px', padding: '10px', border: '1px solid #eee' }}>
        <h4>Simple DatePicker Test:</h4>
        <DatePicker 
          style={{ width: '300px', height: '40px' }}
          placeholder="Select date"
          onChange={handleDateChange}
          value={selectedDate}
        />
        <p style={{ marginTop: '10px', fontSize: '14px', color: '#666' }}>
          Selected: {selectedDate ? selectedDate.format('YYYY-MM-DD') : 'None'}
        </p>
      </div>
      
      {/* RangePicker test */}
      <div style={{ marginBottom: '20px', padding: '10px', border: '1px solid #eee' }}>
        <h4>RangePicker with Time Test:</h4>
        <RangePicker
          showTime={{
            format: 'HH:mm',
            minuteStep: 15,
            use12Hours: false
          }}
          format="YYYY-MM-DD HH:mm"
          disabledDate={disabledDate}
          placeholder={['Start Date & Time', 'End Date & Time']}
          style={{ width: '100%', height: '40px', fontSize: '16px' }}
          onChange={handleRangeChange}
          value={selectedRange}
          getPopupContainer={(trigger) => {
            const container = trigger.closest('.ant-picker') || trigger.parentElement || document.body;
            return container;
          }}
          popupStyle={{
            zIndex: 9999,
            position: 'fixed'
          }}
        />
        <p style={{ marginTop: '10px', fontSize: '14px', color: '#666' }}>
          Selected Range: {selectedRange && selectedRange.length === 2 
            ? `${selectedRange[0].format('YYYY-MM-DD HH:mm')} to ${selectedRange[1].format('YYYY-MM-DD HH:mm')}`
            : 'None'}
        </p>
      </div>

      {/* Test buttons */}
      <div style={{ marginBottom: '20px', padding: '10px', border: '1px solid #eee' }}>
        <h4>Test Actions:</h4>
        <Button 
          onClick={() => {
            console.log('Current selected date:', selectedDate);
            console.log('Current selected range:', selectedRange);
            message.info('Check console for current values');
          }}
          style={{ marginRight: '10px' }}
        >
          Log Current Values
        </Button>
        <Button 
          onClick={() => {
            setSelectedDate(null);
            setSelectedRange(null);
            message.info('Cleared all selections');
          }}
        >
          Clear All
        </Button>
      </div>
      
      <div style={{ padding: '10px', background: '#f0f8ff', border: '1px solid #b3d9ff' }}>
        <h4>Instructions:</h4>
        <ul style={{ margin: '10px 0', paddingLeft: '20px' }}>
          <li>Click on the date input fields above</li>
          <li>Calendar should appear with proper styling</li>
          <li>Time picker should work for the range picker</li>
          <li>You should be able to select dates and times</li>
          <li>If you can type in the fields instead of seeing a calendar, there's an issue</li>
        </ul>
      </div>
    </div>
  );
};

export default TestDatePicker;
