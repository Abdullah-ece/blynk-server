import React from 'react';
import {Widgets} from 'components';
import _ from 'lodash';
import './styles.less';
import {Map} from 'immutable';
import PropTypes from 'prop-types';
import {Icon} from 'antd';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {WidgetsHistory} from 'data/Widgets/api';
import {TimeFiltering} from './scenes';
import {getFormValues, initialize} from 'redux-form';

const DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME = 'device-dashboard-time-filtering';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
  timeFilteringValues: getFormValues(DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME)(state) || {}
}), (dispatch) => ({
  initializeForm: bindActionCreators(initialize, dispatch),
  fetchWidgetHistory: bindActionCreators(WidgetsHistory, dispatch)
}))
class Dashboard extends React.Component {

  static propTypes = {
    dashboard: PropTypes.instanceOf(Map),
    widgets: PropTypes.instanceOf(Map),
    params: PropTypes.object,
    timeFilteringValues: PropTypes.object,
    fetchWidgetHistory: PropTypes.func,
    initializeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      filter: this.FILTERS.HOUR,
      editable: false
    };

    this.handleTimeFilterChange = this.handleTimeFilterChange.bind(this);
  }

  componentWillMount() {

    this.fetchWidgetsData();
  }

  componentDidUpdate(prevProps) {

    if((prevProps.params && prevProps.params.id && Number(prevProps.params.id) !== Number(this.props.params.id)) || !_.isEqual(prevProps.dashboard, this.props.dashboard)) {
      this.fetchWidgetsData();
    }

  }

  fetchWidgetsData(params = {}) {

    const dataQueryRequests = [];

    const dashboard = this.props.dashboard;

    if (dashboard.has('widgets') && dashboard.get('widgets').size)

      dashboard.get('widgets').forEach((widget) => {
        if (widget.has('sources') && widget.get('sources').size) {

          widget.get('sources').forEach((source, sourceIndex) => {
            if (!source || !source.get('dataStream'))
              return null;

            let pin = source.getIn(['dataStream', 'pin']);

            let timeFilter = {};

            if(params.from === undefined) {
              timeFilter = this.getTimeOffsetForData({time: 'HOUR'});
            } else {
              timeFilter = {
                from: params.from,
                to: params.to,
              };
            }

            const additionalParams = {};

            if(source.has('selectedColumns') && source.get('selectedColumns').size) {
              additionalParams.selectedColumns = source.get('selectedColumns').toJS();
            }

            if(source.has('groupByFields') && source.get('groupByFields').size) {
              additionalParams.groupByFields = source.get('groupByFields').toJS();
            }

            if(source.has('sortByFields') && source.get('sortByFields').size) {
              additionalParams.sortByFields = source.get('sortByFields').toJS();
            }

            dataQueryRequests.push({
              "deviceId": this.props.params.id,
              "widgetId": widget.get('id'),
              "sourceIndex": sourceIndex,
              "pinType" : "VIRTUAL",
              "pin" : pin,
              "sortOrder": source.get('sortOrder'),
              "sourceType" : source.get('sourceType'),
              "offset" : 0,
              "limit" : source.get('limit') || 10000,
              ...timeFilter,
              ...additionalParams,
            });

          });
        }
      });

    if (dataQueryRequests.length)
      this.props.fetchWidgetHistory({
        deviceId: this.props.params.id,
        dataQueryRequests: dataQueryRequests,
      });

  }

  FILTERS = {
    HOUR: 'hour',
    DAY: 'day',
    WEEK: 'week',
    MONTH: 'month',
    CUSTOM: 'custom'
  };

  FILTER_BUTTONS = [{
    key: this.FILTERS.HOUR,
    value: 'Last hour'
  }, {
    key: this.FILTERS.DAY,
    value: 'Last day'
  }, {
    key: this.FILTERS.WEEK,
    value: 'Last Week'
  }, {
    key: this.FILTERS.MONTH,
    value: 'Last Month'
  }, {
    key: this.FILTERS.CUSTOM,
    value: 'Custom Range'
  }];

  filterBy(key) {
    this.setState({
      filter: key
    });
  }

  startEditDashboard() {
    this.setState({
      editable: true
    });
  }

  finishEditDashboard() {
    this.setState({
      editable: false
    });
  }

  getTimeOffsetForData(values) {

    const params = {};

    if (values.time === TIMELINE_TIME_FILTERS.CUSTOM.key) {
      if (!values.customTime || !values.customTime.length)
        return false;
      params.from = values.customTime[0];
      params.to = values.customTime[1];
    } else {
      params.from = new Date().getTime() - TIMELINE_TIME_FILTERS[values.time].time;
      params.to = new Date().getTime();
    }

    return params;
  }

  handleTimeFilterChange(values) {

    const params = {
      ...this.getTimeOffsetForData(values) || {}
    };

    this.fetchWidgetsData(params);

  }

  render() {

    let widgets;

    if (this.props.dashboard.has('widgets')) {

      widgets = {
        lg: this.props.dashboard.get('widgets').map((item) => {
          return ({
            ...item.toJS(),
            i: String(item.get('id')),
            id: String(item.get('id')),
            w: item.get('width'),
            h: item.get('height'),
            x: item.get('x'),
            y: item.get('y')
          });
        }).toJS()
      };
    } else {
      widgets = {
        lg: []
      };
    }

    // widgets.lg.push({
    //   typeOfData: 1,
    //   label: 'Devices By Organization',
    //   type: 'BAR',
    //   i: '999',
    //   id: 999,
    //   w: 4,
    //   h: 3,
    //   x: 0,
    //   y: 8,
    // });
    //
    // widgets.lg.push({
    //   typeOfData: 2,
    //   label: 'Devices By Product',
    //   type: 'BAR',
    //   i: '9992',
    //   id: 9992,
    //   w: 4,
    //   h: 3,
    //   x: 4,
    //   y: 8,
    // });
    //
    // widgets.lg.push({
    //   typeOfData: 3,
    //   label: 'Products By Organization',
    //   type: 'BAR',
    //   i: '9993',
    //   id: 9993,
    //   w: 8,
    //   h: 4,
    //   x: 0,
    //   y: 12,
    // });

    let isLoading = false;
    if (this.props.widgets.hasIn([this.props.params.id, 'loading']) && this.props.widgets.getIn([this.props.params.id, 'loading']))
      isLoading = true;

    // uncomment when start to use real data

    if (!isLoading && (!this.props.dashboard.has('widgets') || !this.props.dashboard.get('widgets').size))
      return (
         <div className="devices--device-dashboard">
           <div className="product-no-fields" style={{padding: 0}}>No Dashboard widgets</div>
         </div>
       );

    const params = {
      id: Number(this.props.params.id) || 0
    };

    return (
      <div className="devices--device-dashboard">

        <div>
          <TimeFiltering onChange={this.handleTimeFilterChange} form={DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME}
                         formValues={this.props.timeFilteringValues}/>
        </div>

        { isLoading && (
            <Icon type="loading" className="devices--device-dashboard-loading"/>
        ) || (
          <Widgets params={params} editable={this.state.editable} data={widgets} fetchRealData={true} isPreviewOnly={true}/>
        )}

      </div>
    );
  }

}

export default Dashboard;
