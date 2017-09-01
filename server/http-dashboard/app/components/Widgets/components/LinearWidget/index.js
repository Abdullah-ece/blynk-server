import React from 'react';
import {
  Plotly
} from 'components';
import moment from 'moment';
// import Widget from '../Widget';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';

class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,

    editable: PropTypes.bool,

    onWidgetDelete: PropTypes.func,
  };

  render() {

    const PIN_DATA = {
      "V1": {
        "data": [
          {
            "1504220810168": 5
          },
          {
            "1504220808441": 92
          },
          {
            "1504220803611": 523
          },
          {
            "1504220801358": 143
          },
          {
            "1504220798471": 123
          }
        ]
      }
    };

    const data = [];
    const config = {
      displayModeBar: false
    };
    const layout = {
      autosize: true,
      margin: {
        t: 0,
        r: 0,
        l: 10,
        b: 30,
      },
      yaxis: {
        'title': 'Water Level'
      }
    };

    const getFirstKey = (obj) => Object.keys(obj)[0];

    const x = PIN_DATA.V1.data.map((v) => {
      return moment(Number(getFirstKey(v))).format('HH:mm:ss MM-DD-YYYY');
    });

    const y = [];

    PIN_DATA.V1.data.forEach((v) => {
      return y.push(v[getFirstKey(v)]);
    });

    data.push({
      name: 'Water Level',
      x: x,
      y: y,

      mode: 'lines+markers',
    });


    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={config} layout={layout}/>
      </div>
    );
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;
