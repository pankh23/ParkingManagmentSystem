import React, { useState, useEffect } from 'react';
import { DatePicker, message } from 'antd';
import moment from 'moment';

const { RangePicker } = DatePicker;

const CalendarFixTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  // Add close button to calendar popup
  useEffect(() => {
    if (pickerOpen) {
      const addCloseButton = () => {
        const dropdown = document.querySelector('.ant-picker-dropdown');
        if (dropdown && !dropdown.querySelector('.calendar-close-btn')) {
          const closeButton = document.createElement('button');
          closeButton.className = 'calendar-close-btn';
          closeButton.innerHTML = '✕';
          closeButton.style.cssText = `
            position: absolute;
            top: 6px;
            right: 6px;
            background: #ff4d4f;
            color: white;
            border: none;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            cursor: pointer;
            font-size: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1001;
            transition: all 0.2s ease;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
          `;
          
          closeButton.onmouseenter = () => {
            closeButton.style.background = '#ff7875';
            closeButton.style.transform = 'scale(1.1)';
          };
          
          closeButton.onmouseleave = () => {
            closeButton.style.background = '#ff4d4f';
            closeButton.style.transform = 'scale(1)';
          };
          
          closeButton.onclick = (e) => {
            e.stopPropagation();
            console.log('Calendar close button clicked');
            setPickerOpen(false);
          };
          
          dropdown.style.position = 'relative';
          dropdown.appendChild(closeButton);
        }
      };
      
      // Add close button after a short delay to ensure dropdown is rendered
      setTimeout(addCloseButton, 100);
    }
  }, [pickerOpen]);

  const disabledDate = (current) => {
    return current && current < moment().startOf('day');
  };

  const disabledTime = (current, type) => {
    if (type === 'start') {
      return {
        disabledHours: () => {
          const hours = [];
          const now = moment();
          if (current && current.isSame(now, 'day')) {
            for (let i = 0; i < now.hour(); i++) {
              hours.push(i);
            }
          }
          return hours;
        },
        disabledMinutes: (selectedHour) => {
          const minutes = [];
          const now = moment();
          if (current && current.isSame(now, 'day') && selectedHour === now.hour()) {
            for (let i = 0; i < now.minute(); i++) {
              minutes.push(i);
            }
          }
          return minutes;
        }
      };
    }
    return {};
  };

  const handleRangeChange = (dates) => {
    console.log('Date picker onChange:', dates);
    if (dates && dates.length === 2 && dates[0] && dates[1]) {
      setSelectedRange(dates);
      message.success(`✅ Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
      
      // Close picker after both dates are selected
      console.log('Both dates selected, closing picker');
      setTimeout(() => {
        setPickerOpen(false);
      }, 100);
    } else {
      setSelectedRange(null);
    }
  };

  const handleCalendarChange = (dates) => {
    console.log('Date picker onCalendarChange:', dates);
    if (dates && dates.length === 2 && dates[0] && dates[1]) {
      console.log('Both dates selected in calendar, closing picker');
      setTimeout(() => {
        setPickerOpen(false);
      }, 100);
    }
  };

  const handleOpenChange = (open) => {
    console.log('Date picker open state changed:', open);
    setPickerOpen(open);
  };

  return (
    <div style={{ 
      padding: '20px', 
      border: '2px solid #52c41a', 
      margin: '20px', 
      background: '#fff',
      borderRadius: '8px',
      maxWidth: '600px'
    }}>
      <h2 style={{ color: '#52c41a', marginBottom: '20px' }}>
        🗓️ Calendar Fix Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Range Picker with Fixed Calendar Closing:</h3>
        <RangePicker
          showTime={{
            format: 'HH:mm',
            minuteStep: 15,
            use12Hours: false
          }}
          format="YYYY-MM-DD HH:mm"
          disabledDate={disabledDate}
          disabledTime={disabledTime}
          placeholder={['Start Date & Time', 'End Date & Time']}
          style={{ width: '100%', height: '40px' }}
          value={selectedRange}
          open={pickerOpen}
          onChange={handleRangeChange}
          onCalendarChange={handleCalendarChange}
          onOpenChange={handleOpenChange}
          getPopupContainer={() => document.body}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h4>Status:</h4>
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
          <li>Look for the red ✕ button in the top-right corner of the calendar popup</li>
          <li>Select start date and time, then click OK</li>
          <li>Select end date and time, then click OK</li>
          <li>✅ Calendar should close automatically after selecting both dates</li>
          <li>✅ If calendar doesn't close automatically, click the red ✕ button on the calendar</li>
          <li>✅ Selected dates should be preserved when closing with ✕ button</li>
          <li>✅ You should be able to proceed with booking</li>
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
          <li>✅ Red ✕ button appears in top-right corner of calendar popup</li>
          <li>✅ Calendar closes automatically after selecting both dates</li>
          <li>✅ ✕ button on calendar closes popup and preserves selected dates</li>
          <li>✅ Button has hover effects (scales up and changes color)</li>
          <li>✅ Form is ready for booking after date selection</li>
        </ul>
      </div>
    </div>
  );
};

export default CalendarFixTest;
