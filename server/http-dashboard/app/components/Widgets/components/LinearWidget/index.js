import React from 'react';
import {
  Plotly
} from 'components';
import moment from 'moment';
// import Widget from '../Widget';
import {VIRTUAL_PIN_PREFIX} from 'services/Widgets';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Map} from 'immutable';
import {
  WidgetHistoryByPinFetch
} from 'data/Widgets/api';

@connect((state) => ({
  widgets: state.Widgets.get('widgetsData'),
}), (dispatch) => ({
  fetchWidgetHistoryByPin: bindActionCreators(WidgetHistoryByPinFetch, dispatch)
}))
class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),

    fetchWidgetHistoryByPin: PropTypes.func
  };

  componentWillMount() {

    if (this.props.fetchRealData && this.props.data.sources.length) {

      this.props.data.sources.forEach((source) => {

        if (!source || !source.dataStream)
          return null;

        this.props.fetchWidgetHistoryByPin({
          deviceId: this.props.params.id,
          widgetId: this.props.data.id,
          pin: `${VIRTUAL_PIN_PREFIX}${source.dataStream.pin}`
        });
      });

    }

  }

  render() {

    if (!this.props.data.sources || !this.props.data.sources.length)
      return null;

    let isAnyLoading = false;

    const data = [];

    this.props.data.sources.forEach((source) => {

      const PIN = this.props.widgets.getIn([this.props.params.id, this.props.data.id, `${VIRTUAL_PIN_PREFIX}${source.dataStream.pin}`]);

      if (!PIN) {
        isAnyLoading = true;
        return;
      }

      const loading = PIN.get('loading');

      if (loading || isAnyLoading) {
        isAnyLoading = true;
        return;
      }

      let x = [];
      let y = [];

      PIN.get('data').forEach((item) => {
        x.push(moment(Number(item.get('x'))).format('HH:mm:ss'));
        y.push(item.get('y'));
      });

      data.push({
        name: source.label,
        x: x,
        y: y,
        mode: 'lines+markers',
      });

    });

    if (isAnyLoading)
      return null;

    const config = {
      displayModeBar: false
    };

    const layout = {
      autosize: true,
      margin: {
        t: 15,
        r: 0,
        l: 10,
        b: 30,
      }
    };

    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={config} layout={layout}/>
      </div>
    );
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;


/*
* 1) Get data for own PINS
* 2) Draw data for these own PINS
* 3) Display labels for these PINS
* 4) Fix DataStreams
* 5) Multiple sources support */
