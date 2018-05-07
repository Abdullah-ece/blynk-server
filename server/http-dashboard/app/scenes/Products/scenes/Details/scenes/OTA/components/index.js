import React from 'react';
import {Table, Button, Popconfirm} from 'antd';
import PropTypes from 'prop-types';

import {Status} from 'components/User';

class OTA extends React.Component{

  static propTypes = {
    devices: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      status: PropTypes.oneOf(['ONLINE', 'OFFLINE']), // use this for column "status" and display like a green / gray dot
      disconnectTime: PropTypes.number, // display "Was online N days ago" when user do mouseover the gray dot (idea is to display last time when device was online if it's offline right now)
      hardwareInfo: PropTypes.shape({
        version: PropTypes.string
      })
    }))
  };

  constructor(props) {
    super(props);

    this.state = {
      'selectedRows': 0,
      'usersDeleteLoading': false,
      'sortedInfo': {
        order: 'ascend',
        columnKey: 'name'
      }
    };
  }

  updateColumns() {
    return [{
      title: 'Name',
      dataIndex: 'name',
    }, {
      title: 'Status',
      dataIndex: 'status',

      filters: [{
        text: 'Active',
        value: 'Active',
      }, {
        text: 'Pending',
        value: 'Pending',
      }],
      filterMultiple: false,
      onFilter: (value, record) => record.status === value,

      render: (text, record) => <Status status={record.status}/>
    }, {
      title: 'Firmware version',
      dataIndex: 'firmware_version',
    }];
  }
  // rowSelection = {
  //   onChange: this.onRowSelectionChange.bind(this)
  // };
  //
  // onRowSelectionChange(selectedRowKeys) {
  //   this.setState({
  //     isAnyRowSelected: !!selectedRowKeys.length,
  //     selectedRows: selectedRowKeys
  //   });
  // }


  render(){


    const rowSelection = {
      onChange: (selectedRowKeys, selectedRows) => {
        console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      },
      getCheckboxProps: record => ({
        name: record.name,
      }),
    };
    console.log(this.props);
    const columns = this.updateColumns();
    return (
      <div className="users-profile--organization-settings--organization-users">
        <div className="users-profile--organization-settings--organization-users-delete-button">
          <Popconfirm title="Are you sure you want to delete selected users?"
                      okText="Yes"
                      cancelText="No"
                      overlayClassName="danger">
            <Button type="danger"
                    // disabled={!this.state.selectedRows.length}
                    // loading={this.state.usersDeleteLoading}
            >Update</Button>
          </Popconfirm>
        </div>
        <Table

          rowSelection={rowSelection} columns={columns} dataSource={this.props.devices}
               pagination={false}/>
      </div>
    );
  }
}

export default OTA;
