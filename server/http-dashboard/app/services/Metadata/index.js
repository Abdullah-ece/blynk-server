import moment from 'moment';
import 'moment-duration-format';

export const TimeRange = {

  fromMinutes: (minutes) => (
    moment.duration(minutes, 'minutes').format('HH:mm', {trim: false})
  ),

  toMinutes: (time) => (
    moment.duration(time).asMinutes()
  )

};
