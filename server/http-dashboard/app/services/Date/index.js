import moment from 'moment';

const dataFormatsForCountries = {
  USA: ["MM","DD","YYYY"]
};

const getTimeConfig = (/*dateFormat*/) => {

  return {
    sameDay: 'hh:mm A, [Today]',
    lastDay: '[Yesterday], hh:mm A',
    lastWeek: 'hh:mm A, MMM DD.YYYY',
    sameElse: 'hh:mm A, MMM DD.YYYY'
  };
};

const getDateFormat = (country = "USA", separator = '.') => {
  return dataFormatsForCountries[country].join(separator);
};

export const getFormatedDate = (date = Date.now(), country = "USA", separator = '.') => {

  return moment(Number(date)).format(getDateFormat(country, separator));
};

export const getCalendarFormatDate = (time = Date.now(), /*country = "USA", separator = '.'*/) => {

  return (moment(Number(time)).calendar(null, getTimeConfig(/*getDateFormat(country, separator)*/)));
};
