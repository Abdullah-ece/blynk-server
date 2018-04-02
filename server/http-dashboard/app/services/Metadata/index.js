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

export const Time = {

  fromTimestamp: (time) => (
    moment(time * 1000).format('HH:mm')
  ),

  toTimestamp: (time) => (
    Math.round((moment().hours(time.split(':')[0]).minutes(time.split(':')[1]).valueOf()) )
  )

};
