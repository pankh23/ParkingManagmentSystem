import React, { useState, useEffect } from 'react';
import { DatePicker, message } from 'antd';

const { RangePicker } = DatePicker;

const OKButtonTest = () => {
  const [selectedRange, setSelectedRange] = useState(null);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    // Add global click listener to debug OK button clicks
    const handleClick = (e) => {
      if (e.target.classList.contains('ant-picker-ok') || 
          e.target.classList.contains('ant-picker-ok-btn') ||
          e.target.closest('.ant-picker-ok') ||
          e.target.closest('.ant-picker-ok-btn')) {
        console.log('OK button clicked!', e.target);
        message.success('OK button clicked!');
      }
    };

    document.addEventListener('click', handleClick);
    return () => document.removeEventListener('click', handleClick);
  }, []);

  const handleRangeChange = (dates) => {
    console.log('Range changed:', dates);
    setSelectedRange(dates);
    if (dates && dates.length === 2) {
      message.success(`Selected: ${dates[0].format('YYYY-MM-DD HH:mm')} to ${dates[1].format('YYYY-MM-DD HH:mm')}`);
    }
  };

  const handleOpenChange = (open) => {
    console.log('Picker open state:', open);
    setIsOpen(open);
    
    if (open) {
      // Add debugging for OK button when picker opens
      setTimeout(() => {
        const dropdown = document.querySelector('.ant-picker-dropdown');
        if (dropdown) {
          console.log('Dropdown found:', dropdown);
          
          const okButton = dropdown.querySelector('.ant-picker-ok, .ant-picker-ok-btn');
          if (okButton) {
            console.log('OK button found:', okButton);
            console.log('OK button styles:', {
              display: getComputedStyle(okButton).display,
              visibility: getComputedStyle(okButton).visibility,
              opacity: getComputedStyle(okButton).opacity,
              pointerEvents: getComputedStyle(okButton).pointerEvents,
              zIndex: getComputedStyle(okButton).zIndex
            });
          } else {
            console.log('OK button not found');
          }
        }
      }, 100);
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
        🔘 OK Button Test
      </h2>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Range Picker with OK Button Debug:</h3>
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
          onOpenChange={handleOpenChange}
          getPopupContainer={() => document.body}
        />
      </div>

      <div style={{ marginBottom: '20px' }}>
        <h4>Status:</h4>
        <p>Picker is {isOpen ? 'OPEN' : 'CLOSED'}</p>
        <p>Selected Range: {selectedRange && selectedRange.length === 2 
          ? `${selectedRange[0].format('YYYY-MM-DD HH:mm')} to ${selectedRange[1].format('YYYY-MM-DD HH:mm')}`
          : 'None'}
        </p>
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
          <li>Select start and end dates/times</li>
          <li>Click the OK button in the calendar</li>
          <li>Check console for debug messages</li>
          <li>You should see "OK button clicked!" message</li>
        </ol>
      </div>

      <div style={{ 
        background: '#fff3cd', 
        padding: '15px', 
        borderRadius: '5px',
        border: '1px solid #ffeaa7',
        marginTop: '15px'
      }}>
        <h4>Debug Info:</h4>
        <p>Check browser console for detailed debugging information about the OK button.</p>
      </div>
    </div>
  );
};

export default OKButtonTest;
