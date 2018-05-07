import moment from 'moment';

export const convertTimeStampToTime = (timestamp) => {
  if(timestamp / 1000 / 60 > 1){
    return moment.duration(timestamp / 1000, "seconds").format("d[d] h[h] m[min]");
  } else {
    return Math.floor(timestamp / 1000) + " seconds";
  }
};
