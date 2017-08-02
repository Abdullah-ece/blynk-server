import React from 'react';
import {Select} from 'antd';
import './styles.less'

class DevicesSearch extends React.Component {

  render() {
    return (
      <div className="devices-search">
        <Select style={{width: '100%'}} placeholder="Search..." mode="multiple"
                notFoundContent="Search is on development"/>
      </div>
    );
  }

}

export default DevicesSearch;
