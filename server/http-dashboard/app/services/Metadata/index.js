import moment from 'moment';

export const TimeRange = {

  fromMinutes: (minutes) => (
    moment.duration(minutes, 'minutes').format('HH:mm')
  ),

  toMinutes: (time) => (
    moment.duration(time).asMinutes()
  )

};
