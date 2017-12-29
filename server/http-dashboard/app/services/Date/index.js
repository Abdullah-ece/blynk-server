import moment from 'moment';

const dataFormatsForCountries = {
  USA: ["MM","DD","YYYY"]
};

const getTimeConfig = (dateFormat) => {

  return {
    sameDay: '[Today], hh:mm A',
    lastDay: '[Yesterday], hh:mm A',
    lastWeek: 'dddd, hh:mm A',
    sameElse: 'hh:mm A, ' + dateFormat
  };
};

const getDateFormat = (country = "USA", separator = '.') => {
  return dataFormatsForCountries[country].join(separator);
};

export const getFormatedDate = (date = Date.now(), country = "USA", separator = '.') => {

  return moment(Number(date)).format(getDateFormat(country, separator));
};

export const getCalendarFormatDate = (time = Date.now(), country = "USA", separator = '.') => {

  return (moment(Number(time)).calendar(null, getTimeConfig(getDateFormat(country, separator))));
};
