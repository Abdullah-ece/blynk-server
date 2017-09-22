import React from 'react';
import {Icon} from 'antd';
import {
  Plotly
} from 'components';
import PropTypes from 'prop-types';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Map} from 'immutable';
import {
  WidgetProductsFetch,
  WidgetOrganizationFetch,
  WidgetOrganizationsFetch,
} from 'data/Widgets/api';

@connect((state) => ({
  orgId: state.Account.orgId,
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}), (dispatch) => ({
  fetchProductsForWidget: bindActionCreators(WidgetProductsFetch, dispatch),
  fetchOrganization: bindActionCreators(WidgetOrganizationFetch, dispatch),
  fetchOrganizationsList: bindActionCreators(WidgetOrganizationsFetch, dispatch),
}))
class BarWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    orgId: PropTypes.number,
    typeOfData: PropTypes.number,

    fetchProductsForWidget: PropTypes.func,
    fetchOrganizationsList: PropTypes.func,
    fetchOrganization: PropTypes.func,
    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  state = {
    loading: true,
    data: []
  };

  componentWillMount() {
    this.setState({
      loading: true
    });

    if (this.props.data.typeOfData === 2) {
      this.props.fetchProductsForWidget().then(() => {
        this.setState({
          loading: false
        });
      });
    }

    if (this.props.data.typeOfData === 1 || this.props.data.typeOfData === 3) {
      const params = {
        orgId: this.props.orgId,
        deviceId: this.props.data.id
      };

      Promise.all([
        this.props.fetchOrganization(params),
        this.props.fetchOrganizationsList(params),
      ]).then(() => {
        this.setState({
          loading: false
        });
      });
    }

  }

  layout = {
    margin: {
      t: 0,
      b: 25,
      r: 15,
      l: 65,
    },
    yaxis: {
      showline: true,
      linecolor: 'rgb(204,204,204)',
      ticklen: 3,
      tickcolor: '#fff',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#9a9a9a'
      },
      zeroline: false,
    },
    xaxis: {
      zeroline: false,
      showline: true,
      linecolor: 'rgb(204,204,204)',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '10',
        color: '#9a9a9a'
      },
      ticklen: 3,
      tickangle: 0,
      nticks: 12,
      tickcolor: '#fff',
    },
  };

  dataChartConfig = {};

  config = {
    displayModeBar: false
  };

  legendConfig = {
    width: [0.27, 0.27, 0.27],
    type: 'bar',
    orientation: 'h'
  };

  renderChartByParams(data = [], config = {}, layout = {}) {

    let maxYLabelWidth = 0;

    data.map((legend) => {
      // calculate max Y label width
      if (legend && Array.isArray(legend.y) && legend.y.length) {
        legend.y.forEach((label) => {
          if (String(label).length)
            maxYLabelWidth = label.length > maxYLabelWidth ? label.length : maxYLabelWidth;
        });
      }

      //calculate width of bars

      const countOfYLabels = legend && Array.isArray(legend.y) && legend.y.length || 1;

      const CELL_HEIGHT = 101;
      const TOP_PADDING = 50;
      const BOTTOM_PADDING = 30;
      const CHART_HEIGHT = this.props.data.h * CELL_HEIGHT - TOP_PADDING - BOTTOM_PADDING;
      const PREFERED_BAR_WIDTH = 20;
      const MIN_RATIO_BAR_WIDTH_TO_CHART = 0.25;

      const percentageWidthOfBar = PREFERED_BAR_WIDTH / (CHART_HEIGHT / countOfYLabels);

      if (percentageWidthOfBar <= MIN_RATIO_BAR_WIDTH_TO_CHART) {

        legend.width = (new Array(countOfYLabels)).fill(MIN_RATIO_BAR_WIDTH_TO_CHART);

      } else {
        legend.width = (new Array(countOfYLabels)).fill(percentageWidthOfBar);
      }

      return legend;
    });

    if (layout && layout.margin && layout.margin.l) {
      layout.margin.l = Math.round(maxYLabelWidth * 6);
    }

    return (
      <div className="grid-linear-widget">
        <Plotly data={data} config={config} layout={layout}/>
      </div>
    );
  }

  renderDevicesByOrganization() {

    const data = [
      {
        x: [345, 321, 24],
        y: ['Blynk Inc.', 'Knight LLC', 'Goooogle'],
        ...this.legendConfig,
      }
    ];

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);

  }

  renderDevicesByProduct() {
    const data = [
      {
        x: [],
        y: [],
        ...this.legendConfig,
      }
    ];

    this.props.widgets.getIn([String(this.props.data.id), 'data']).forEach((item) => {
      data[0].x.push(item.get('deviceCount'));
      data[0].y.push(item.get('name'));
    });

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  renderProductByOrganization() {
    const data = [
      {
        x: [],
        y: [],
        ...this.legendConfig,
      }
    ];

    const addOrgToChart = (organization) => {

      const productsCount = organization && organization.get('products') && organization.get('products').size || 0;

      data[0].x.push(productsCount);
      data[0].y.push(organization.get('name'));
    };

    const orgs = this.props.widgets.getIn([String(this.props.data.id), 'organizations']);
    const currentOrg = this.props.widgets.getIn([String(this.props.data.id), 'organization']);

    if (orgs && orgs.forEach)
      orgs.forEach(addOrgToChart);

    if (currentOrg && currentOrg.get('name'))
      addOrgToChart(currentOrg);


    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  renderChartPreload() {
    return (
      <div className="grid-linear-widget">
        <div className="widget--chart-loading"><Icon type="loading"/></div>
      </div>
    );
  }

  render() {

    if (this.state.loading)
      return this.renderChartPreload();

    if (parseInt(this.props.data.typeOfData) === 1)
      return this.renderDevicesByOrganization();

    if (parseInt(this.props.data.typeOfData) === 2)
      return this.renderDevicesByProduct();

    // otherwise
    return this.renderProductByOrganization();
  }
}

export default BarWidget;
