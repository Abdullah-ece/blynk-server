import React from 'react';
import {Icon} from 'antd';
import './styles.less';
import {connect} from 'react-redux';

@connect((state) => ({
  isPageLoading: state.PageLoading.isPageLoading
}))
class PageLoading extends React.Component {

  static propTypes = {
    isPageLoading: React.PropTypes.bool
  };

  constructor(props) {
    super(props);

    this.state = {
      isPageLoading: this.props.isPageLoading
    };
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.isPageLoading && !nextProps.isPageLoading) {
      setTimeout(() => {
        this.setState({
          isPageLoading: false
        });
      }, 300);
    } else if (nextProps.isPageLoading) {
      this.setState({
        isPageLoading: true
      });
    }
  }

  shouldComponentUpdate(nextProps, nextState) {
    return nextState.isPageLoading !== this.state.isPageLoading;
  }

  render() {

    if (this.state.isPageLoading)
      return (
        <div className="page-loading">
          <Icon type="loading"/>
        </div>
      );

    return null;
  }

}

export default PageLoading;
