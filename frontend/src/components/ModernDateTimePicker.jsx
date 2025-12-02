import React, { useState, useRef, useEffect } from 'react';
import moment from 'moment';
import './ModernDateTimePicker.css';

const ModernDateTimePicker = ({ value, onChange, placeholder, disabledDate, disabledTime }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [startDate, setStartDate] = useState(value?.[0] || null);
  const [endDate, setEndDate] = useState(value?.[1] || null);
  const [currentMonth, setCurrentMonth] = useState(moment());
  const [startTime, setStartTime] = useState({ hour: 0, minute: 0 });
  const [endTime, setEndTime] = useState({ hour: 23, minute: 45 });
  const [activePanel, setActivePanel] = useState('start'); // 'start' or 'end'
  const [sidebarWidth, setSidebarWidth] = useState(256); // Default sidebar width (w-64 = 256px)
  const pickerRef = useRef(null);
  const dropdownRef = useRef(null);
  const hourRef = useRef(null);
  const minuteRef = useRef(null);

  useEffect(() => {
    if (value && value.length === 2) {
      setStartDate(value[0]);
      setEndDate(value[1]);
      if (value[0]) {
        setStartTime({ hour: value[0].hour(), minute: Math.floor(value[0].minute() / 15) * 15 });
      }
      if (value[1]) {
        setEndTime({ hour: value[1].hour(), minute: Math.floor(value[1].minute() / 15) * 15 });
      }
    }
  }, [value]);

  useEffect(() => {
    if (hourRef.current && isOpen) {
      const time = activePanel === 'start' ? startTime : endTime;
      const scrollPosition = time.hour * 40;
      hourRef.current.scrollTop = scrollPosition;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activePanel, isOpen]);

  useEffect(() => {
    if (minuteRef.current && isOpen) {
      const time = activePanel === 'start' ? startTime : endTime;
      const minutes = [0, 15, 30, 45];
      const minuteIndex = minutes.indexOf(time.minute);
      const scrollPosition = minuteIndex * 40;
      minuteRef.current.scrollTop = scrollPosition;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activePanel, isOpen]);

  useEffect(() => {
    // Auto-close when both dates and times are selected
    if (startDate && endDate && startTime && endTime && isOpen) {
      const finalStart = startDate.clone().hour(startTime.hour).minute(startTime.minute);
      const finalEnd = endDate.clone().hour(endTime.hour).minute(endTime.minute);
      if (finalStart.isBefore(finalEnd)) {
        // Both dates and times are set, close after a brief delay
        const timer = setTimeout(() => {
          if (onChange) {
            onChange([finalStart, finalEnd]);
          }
          setIsOpen(false);
        }, 300);
        return () => clearTimeout(timer);
      }
    }
  }, [startDate, endDate, startTime, endTime, isOpen, onChange]);

  useEffect(() => {
    // Detect sidebar width
    const detectSidebarWidth = () => {
      const sidebar = document.querySelector('aside');
      if (sidebar) {
        const width = sidebar.offsetWidth;
        setSidebarWidth(width);
      } else {
        // Fallback: check for common sidebar classes
        const sidebar64 = document.querySelector('.w-64');
        const sidebar20 = document.querySelector('.w-20');
        if (sidebar64) {
          setSidebarWidth(256); // w-64 = 256px
        } else if (sidebar20) {
          setSidebarWidth(80); // w-20 = 80px
        }
      }
    };

    detectSidebarWidth();
    
    // Watch for sidebar changes
    const observer = new MutationObserver(detectSidebarWidth);
    const sidebar = document.querySelector('aside');
    if (sidebar) {
      observer.observe(sidebar, {
        attributes: true,
        attributeFilter: ['class']
      });
    }

    window.addEventListener('resize', detectSidebarWidth);

    return () => {
      observer.disconnect();
      window.removeEventListener('resize', detectSidebarWidth);
    };
  }, []);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target) &&
          pickerRef.current && !pickerRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    const updatePosition = () => {
      if (dropdownRef.current && pickerRef.current && isOpen) {
        const pickerRect = pickerRef.current.getBoundingClientRect();
        const dropdown = dropdownRef.current;
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;
        const dropdownWidth = 700;
        const estimatedDropdownHeight = 550; // Approximate height of the picker
        
        // Position left of dropdown = sidebar width + margin
        let leftPos = sidebarWidth + 20;
        
        // Check if dropdown would go off screen on the right
        if (leftPos + dropdownWidth > viewportWidth - 20) {
          // Position from right instead
          leftPos = viewportWidth - dropdownWidth - 20;
        }
        
        // Calculate vertical position
        let topPos = pickerRect.bottom + 8;
        
        // Check if dropdown would go off screen at the bottom
        if (topPos + estimatedDropdownHeight > viewportHeight - 20) {
          // Position above the input field instead
          topPos = pickerRect.top - estimatedDropdownHeight - 8;
          
          // If still off screen at top, position at top of viewport with margin
          if (topPos < 20) {
            topPos = 20;
            dropdown.style.maxHeight = `${viewportHeight - 40}px`;
            dropdown.style.overflowY = 'auto';
          } else {
            dropdown.style.maxHeight = 'none';
            dropdown.style.overflowY = 'visible';
          }
        } else {
          dropdown.style.maxHeight = `${viewportHeight - topPos - 20}px`;
          dropdown.style.overflowY = 'auto';
        }
        
        dropdown.style.left = `${leftPos}px`;
        dropdown.style.right = 'auto';
        dropdown.style.top = `${topPos}px`;
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
      updatePosition();
      window.addEventListener('resize', updatePosition);
      window.addEventListener('scroll', updatePosition, true);
      
      setTimeout(updatePosition, 10);
      
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
        window.removeEventListener('resize', updatePosition);
        window.removeEventListener('scroll', updatePosition, true);
      };
    }
  }, [isOpen, sidebarWidth]);

  const isDateDisabled = (date) => {
    if (!disabledDate) return false;
    return disabledDate(date);
  };

  const isTimeDisabled = (hour, minute, isStart) => {
    if (!disabledTime) return false;
    const testDate = moment().hour(hour).minute(minute);
    const disabled = disabledTime(testDate, isStart ? 'start' : 'end');
    if (disabled && disabled.disabledHours) {
      return disabled.disabledHours().includes(hour);
    }
    return false;
  };

  const handleDateClick = (date) => {
    if (isDateDisabled(date)) return;

    const newDate = date.clone();
    if (activePanel === 'start') {
      newDate.hour(startTime.hour).minute(startTime.minute);
      setStartDate(newDate);
      if (!endDate || newDate.isAfter(endDate)) {
        const newEndDate = newDate.clone().add(1, 'hour');
        setEndDate(newEndDate);
        setEndTime({ hour: newEndDate.hour(), minute: Math.floor(newEndDate.minute() / 15) * 15 });
      }
    } else {
      newDate.hour(endTime.hour).minute(endTime.minute);
      if (startDate && newDate.isBefore(startDate)) {
        return;
      }
      setEndDate(newDate);
    }
  };

  const handleTimeChange = (type, field, val) => {
    if (type === 'start') {
      const newTime = { ...startTime, [field]: val };
      setStartTime(newTime);
      if (startDate) {
        const newDate = startDate.clone().hour(newTime.hour).minute(newTime.minute);
        setStartDate(newDate);
      }
    } else {
      const newTime = { ...endTime, [field]: val };
      setEndTime(newTime);
      if (endDate) {
        const newDate = endDate.clone().hour(newTime.hour).minute(newTime.minute);
        setEndDate(newDate);
      }
    }
  };

  const handleOK = () => {
    if (startDate && endDate && startDate.isBefore(endDate)) {
      const finalStart = startDate.clone().hour(startTime.hour).minute(startTime.minute).second(0);
      const finalEnd = endDate.clone().hour(endTime.hour).minute(endTime.minute).second(0);
      if (onChange) {
        onChange([finalStart, finalEnd]);
      }
      setIsOpen(false);
    }
  };

  const handleClear = () => {
    setStartDate(null);
    setEndDate(null);
    setStartTime({ hour: 0, minute: 0 });
    setEndTime({ hour: 23, minute: 45 });
    if (onChange) {
      onChange(null);
    }
  };

  const renderCalendar = () => {
    const startOfMonth = currentMonth.clone().startOf('month');
    const endOfMonth = currentMonth.clone().endOf('month');
    const startOfCalendar = startOfMonth.clone().startOf('week');
    const endOfCalendar = endOfMonth.clone().endOf('week');
    const days = [];
    const day = startOfCalendar.clone();

    while (day.isSameOrBefore(endOfCalendar, 'day')) {
      days.push(day.clone());
      day.add(1, 'day');
    }

    const weekDays = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];

    return (
      <div>
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={() => setCurrentMonth(currentMonth.clone().subtract(1, 'month'))}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            type="button"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h3 className="text-lg font-semibold text-gray-800">
            {currentMonth.format('MMM YYYY')}
          </h3>
          <button
            onClick={() => setCurrentMonth(currentMonth.clone().add(1, 'month'))}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            type="button"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>

        <div className="grid grid-cols-7 gap-2 mb-2">
          {weekDays.map((day) => (
            <div key={day} className="text-center text-xs font-semibold text-gray-500 py-2">
              {day}
            </div>
          ))}
        </div>

        <div className="grid grid-cols-7 gap-2">
          {days.map((day, idx) => {
            const isCurrentMonth = day.isSame(currentMonth, 'month');
            const isToday = day.isSame(moment(), 'day');
            const isStart = startDate && day.isSame(startDate, 'day');
            const isEnd = endDate && day.isSame(endDate, 'day');
            const isInRange = startDate && endDate && day.isAfter(startDate, 'day') && day.isBefore(endDate, 'day');
            const isDisabled = isDateDisabled(day);

            return (
              <button
                key={idx}
                onClick={() => handleDateClick(day)}
                disabled={isDisabled || !isCurrentMonth}
                className={`
                  h-10 w-full rounded-lg text-sm font-medium transition-all flex items-center justify-center
                  ${!isCurrentMonth ? 'text-gray-300' : 'text-gray-700'}
                  ${isDisabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer hover:bg-gray-100'}
                  ${isToday && !isStart && !isEnd ? 'border-2 border-blue-400' : ''}
                  ${isStart ? 'bg-blue-600 text-white hover:bg-blue-700' : ''}
                  ${isEnd ? 'bg-blue-600 text-white hover:bg-blue-700' : ''}
                  ${isInRange ? 'bg-blue-50 text-blue-700' : ''}
                `}
                type="button"
              >
                {day.format('D')}
              </button>
            );
          })}
        </div>
      </div>
    );
  };

  const renderTimePicker = (type) => {
    const time = type === 'start' ? startTime : endTime;
    const hours = Array.from({ length: 24 }, (_, i) => i);
    const minutes = [0, 15, 30, 45];

    return (
      <div className="flex gap-4 p-4">
        <div className="flex-1">
          <div className="text-xs font-semibold text-gray-500 mb-2 uppercase">Hour</div>
          <div ref={hourRef} className="h-48 overflow-y-auto border border-gray-200 rounded-lg">
            {hours.map((hour) => {
              const disabled = isTimeDisabled(hour, 0, type === 'start');
              return (
                <button
                  key={hour}
                  onClick={() => !disabled && handleTimeChange(type, 'hour', hour)}
                  disabled={disabled}
                  className={`
                    w-full py-2 text-sm font-medium transition-colors
                    ${time.hour === hour ? 'bg-blue-600 text-white' : 'hover:bg-gray-100 text-gray-700'}
                    ${disabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer'}
                  `}
                  type="button"
                >
                  {String(hour).padStart(2, '0')}
                </button>
              );
            })}
          </div>
        </div>
        <div className="flex-1">
          <div className="text-xs font-semibold text-gray-500 mb-2 uppercase">Minute</div>
          <div ref={minuteRef} className="h-48 overflow-y-auto border border-gray-200 rounded-lg">
            {minutes.map((minute) => (
              <button
                key={minute}
                onClick={() => handleTimeChange(type, 'minute', minute)}
                className={`
                  w-full py-2 text-sm font-medium transition-colors
                  ${time.minute === minute ? 'bg-blue-600 text-white' : 'hover:bg-gray-100 text-gray-700'}
                  cursor-pointer
                `}
                type="button"
              >
                {String(minute).padStart(2, '0')}
              </button>
            ))}
          </div>
        </div>
      </div>
    );
  };

  const formatDisplayValue = () => {
    if (startDate && endDate) {
      return `${startDate.format('MMM DD, YYYY HH:mm')} - ${endDate.format('MMM DD, YYYY HH:mm')}`;
    }
    return placeholder || 'Select date and time range';
  };

  return (
    <div className="relative modern-datetime-picker" ref={pickerRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="w-full h-12 px-4 text-left bg-white border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-all duration-200 shadow-sm flex items-center justify-between"
        type="button"
      >
        <span className={startDate && endDate ? 'text-gray-800' : 'text-gray-500'}>
          {formatDisplayValue()}
        </span>
        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </button>

      {isOpen && (
        <div
          ref={dropdownRef}
          className="fixed z-50 bg-white rounded-2xl shadow-2xl border border-gray-200"
          style={{ 
            width: '700px',
            maxWidth: `calc(100vw - ${sidebarWidth + 40}px)`
          }}
        >
          <div className="flex">
            <div className="flex-1 border-r border-gray-200 p-5" style={{ minWidth: '350px', maxWidth: '350px' }}>
              {renderCalendar()}
            </div>
            <div className="flex-1" style={{ minWidth: '280px', maxHeight: '550px', overflowY: 'auto' }}>
              <div className="border-b border-gray-200 p-4">
                <div className="flex gap-2">
                  <button
                    onClick={() => setActivePanel('start')}
                    className={`
                      flex-1 py-2 px-4 rounded-lg font-medium text-sm transition-colors
                      ${activePanel === 'start' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}
                    `}
                    type="button"
                  >
                    Start Time
                  </button>
                  <button
                    onClick={() => setActivePanel('end')}
                    className={`
                      flex-1 py-2 px-4 rounded-lg font-medium text-sm transition-colors
                      ${activePanel === 'end' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'}
                    `}
                    type="button"
                  >
                    End Time
                  </button>
                </div>
              </div>
              {renderTimePicker(activePanel)}
            </div>
          </div>
          <div className="border-t border-gray-200 p-4 flex justify-end gap-2 bg-white">
            <button
              onClick={handleClear}
              className="px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg font-medium transition-colors"
              type="button"
            >
              Clear
            </button>
            <button
              onClick={handleOK}
              disabled={!startDate || !endDate || startDate.isAfter(endDate)}
              className={`
                px-6 py-2 rounded-lg font-semibold text-white transition-all
                ${startDate && endDate && startDate.isBefore(endDate)
                  ? 'bg-blue-600 hover:bg-blue-700 shadow-md hover:shadow-lg cursor-pointer'
                  : 'bg-gray-300 cursor-not-allowed'}
              `}
              type="button"
            >
              OK
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ModernDateTimePicker;

