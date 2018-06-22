import React from 'react';
import {Button, Table, Checkbox} from 'antd';
import {Cards, SimpleContentEditable} from 'components';
import PropTypes from 'prop-types';
import Scroll from "react-scroll";
import {reduxForm, FieldArray, Field} from 'redux-form';
import './styles.less';

const PERMISSION_TYPES = {
  VIEW: {
    type: 'VIEW',
    mask: 100,
  },
  EDIT: {
    type: 'EDIT',
    mask: 10,
  },
  DELETE: {
    type: 'DELETE',
    mask: 1,
  },
};

@reduxForm()
class RolesAndPermissions extends React.Component {

  static propTypes = {
    onAddRole: PropTypes.func,

    formValues: PropTypes.shape({
      roles: PropTypes.arrayOf(PropTypes.shape({
        id              : PropTypes.number,
        name            : PropTypes.string,
        devices         : PropTypes.any,
        products        : PropTypes.any,
        suborganizations: PropTypes.any,
        organization    : PropTypes.any,
      }))
    })
  };

  constructor(props) {
    super(props);

    this.renderList = this.renderList.bind(this);
    this.handleAddRole = this.handleAddRole.bind(this);
  }

  componentDidUpdate(prevProps) {

    if(prevProps && prevProps.formValues && prevProps.formValues.roles) {
      let newId = null;

      const lastIds = prevProps.formValues.roles.map((role) => {
        return Number(role.id);
      });

      this.props.formValues.roles.forEach((role) => {
        if(lastIds.indexOf(Number(role.id)) === -1) {
          newId = role.id;
        }
      });

      if(newId) {

        Scroll.scroller.scrollTo(`role-${newId}`, {
          duration: 1000,
          offset: -32,
          smooth: "easeInOutQuint",
        });
      }
    }

  }

  roleNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="user-profile--roles-and-permissions--roles-list--role--title"
                             value={input.value}
                             onChange={input.onChange}/>
    );
  }

  handleAddRole() {
    if (typeof this.props.onAddRole === 'function')
      this.props.onAddRole();
  }

  // handleSortStart({index, node, collection}, event) {
  //
  // }

  permissionCheckbox({ input, permissionType }) {

    const getValue = () => {
      return (input.value & permissionType.mask) === permissionType.mask;
    };

    const onChange = (event) => {

      const checked = event.target.checked;

      if((input.value & permissionType.mask) && !checked) {
        input.onChange(input.value - permissionType.mask);
      } else if(!(input.value & permissionType.mask) && checked) {
        input.onChange(input.value + permissionType.mask);
      }
    };

    const value = !!getValue();

    return (<Checkbox checked={value} onChange={onChange}/>);
  }

  renderList({fields}) {
    const prepareData = (field) => {
      return ([{
        key   : '1',
        name  : 'Devices',
        view  : {
          field: field,
          type: 'devices'
        },
        edit  : {
          field: field,
          type: 'devices'
        },
        remove: {
          field: field,
          type: 'devices'
        },
      }, {
        key   : '2',
        name  : 'Products',
        view  : {
          field: field,
          type: 'products'
        },
        edit  : {
          field: field,
          type: 'products'
        },
        remove: {
          field: field,
          type: 'products'
        },
      }, {
        key   : '3',
        name  : 'Sub Organizations',
        view  : {
          field: field,
          type: 'suborganizations'
        },
        edit  : {
          field: field,
          type: 'suborganizations'
        },
        remove: {
          field: field,
          type: 'suborganizations'
        },
      }, {
        key   : '4',
        name  : 'Organization',
        view  : {
          field: field,
          type: 'organization'
        },
        edit  : {
          field: field,
          type: 'organization'
        },
        remove: {
          field: field,
          type: 'organization'
        },
      }]);
    };

    const columns = [
      {
      // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Permission name</p>),
      title    : (<p>Permission name</p>),
      dataIndex: 'name',
      // render   : (value) => (<p className="user-profile--roles-and-permissions--roles-list--role--table-name-clickable" onClick={() => ({})}>{value}</p>)
      render   : (value) => (<p>{value}</p>)
    }, {
      // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>View</p>),
      title    : (<p>View</p>),
      dataIndex: 'view',
      render   : ({field, type}) => {
        return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
                       permissionType={PERMISSION_TYPES.VIEW}/>);
      }
    }, {
      // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Edit</p>),
      title    : (<p>Edit</p>),
      dataIndex: 'edit',
      render   : ({field, type}) => {
        return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
                       permissionType={PERMISSION_TYPES.EDIT}/>);
      }
    }, {
      // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Delete</p>),
      title    : (<p>Delete</p>),
      dataIndex: 'remove',
      render   : ({field, type}) => {
        return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
                       permissionType={PERMISSION_TYPES.DELETE}/>);
      }
    }];

    const handleAddRole = () => {
      fields.push({
        id: new Date().getTime(),
        name: 'Role',
        devices         : 111,
        products        : 111,
        suborganizations: 111,
        organization    : 111,
      });
    };

    const handleSortEnd = ({oldIndex, newIndex}) => {
      fields.swap(oldIndex, newIndex);
    };

    const handleRemove = (index) => {
      fields.remove(index);
    };

    const handleCopy = (index) => {
      let field = fields.get(index);

      let name = `${field.name} Copy`;

      fields.insert(index + 1,
        {
        ...field,
        name: name,
        id: new Date().getTime()
      });
    };

    return (
      <div className="user-profile--roles-and-permissions">
        <div className="user-profile--roles-and-permissions--add-button">
          <Button onClick={handleAddRole}>Add Role</Button>
        </div>

        <div className="user-profile--roles-and-permissions--roles-list">

          <Cards onSortEnd={handleSortEnd} /*onSortStart={this.handleSortStart}*/>

            {fields.map((field, index, fields) => {

              const role = fields.get(index);

              return (
                <Cards.Item key={role.id}
                            cardId={index}
                            draggable={!role.isDefault}
                            removeable={!role.isDefault}
                            copyable={!role.isDefault}
                            onRemove={handleRemove}
                            onCopy={handleCopy}>
                  <Scroll.Element name={`role-${role.id}`}>
                    <div className="user-profile--roles-and-permissions--roles-list--role">
                      {role.isDefault && (
                        <div className="user-profile--roles-and-permissions--roles-list--role--title">{role.name}</div>
                      ) || (
                        <Field name={`${field}.name`} component={this.roleNameComponent}/>
                      )}
                      <div className="user-profile--roles-and-permissions--roles-list--role--table">
                        <Table dataSource={prepareData(field, role)} columns={columns} size="small" pagination={false}/>
                      </div>
                    </div>
                  </Scroll.Element>
                </Cards.Item>
              );
            })}
          </Cards>

        </div>
      </div>
    );
  }

  render() {

    return (
      <FieldArray component={this.renderList} name="roles" rerenderOnEveryChange={true}/>
    );
  }

}

export default RolesAndPermissions;
