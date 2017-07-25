import React                  from 'react';
import {List}                 from 'immutable';
import {connect}              from 'react-redux';
import PropTypes              from 'prop-types';
import {bindActionCreators}   from 'redux';

import {
  List as OrgsList,
  Empty
}                             from './scenes';

import {
  OrganizationsFetch
}                             from 'data/Organizations/actions';
import {
  StartLoading,
  FinishLoading
}                             from 'data/PageLoading/actions';

@connect((state) => ({
  list: state.Organizations.get('list')
}), (dispatch) => ({
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  startLoading: bindActionCreators(StartLoading, dispatch),
  finishLoading: bindActionCreators(FinishLoading, dispatch)
}))
class Index extends React.Component {

  static propTypes = {
    list: PropTypes.instanceOf(List),
    OrganizationsFetch: PropTypes.func,
    startLoading: PropTypes.func,
    finishLoading: PropTypes.func,

    location: PropTypes.object
  };

  componentWillMount() {
    if (!this.props.list)
      this.fetch();
  }

  fetch() {
    this.props.startLoading();
    this.props.OrganizationsFetch().then(() => this.props.finishLoading());
  }

  render() {

    // while no data display null
    if (!this.props.list)
      return null;

    // if has organizations display list scene
    if (this.props.list.size)
      return (
        <OrgsList data={this.props.list} location={this.props.location}/>
      );

    // if has no organizations display empty scene
    if (!this.props.list.size)
      return (
        <Empty />
      );

  }

}

export default Index;
