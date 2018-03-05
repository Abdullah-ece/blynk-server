import React from 'react';

import ui from 'components/UI';
import Validation from 'services/Validation';

import {Button, Input, Checkbox, Select, Radio, Switch} from 'antd';

import './styles.less';

class StyleGuide extends React.Component {
  render() {
    const Option = Select.Option;
    const children = [];
    for (let i = 10; i < 36; i++) {
      children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
    }

    return (
      <div className="style-guide">
        <div className="style-guide-element">
          <Select
            mode="tags"
            style={{ width: '100%' }}
            placeholder="Tags Mode"
          >
            {children}
          </Select>
        </div>
        <div className="style-guide-element">
          <Switch />
          <br />
          <Switch size="small" />
        </div>
        <div className="style-guide-element">
          <Button type="primary">Button</Button> <Button type="primary" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button type="primary" icon="plus">Button</Button> <Button type="primary" icon="plus" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button>Button</Button> <Button disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button icon="edit"/>
        </div>
        <div className="style-guide-element dark">
          <Button icon="edit" className="dark"/> <Button icon="user" className="dark"/>
        </div>
        <div className="style-guide-element">
          <Button type="danger">Button</Button> <Button type="danger" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button type="dashed" icon="plus">Add Meta data</Button>
        </div>
        <div className="style-guide-element">
          <Input placeholder="Label" style={{width: 168}}/>
        </div>
        <div className="style-guide-element">
          <Input placeholder="Label" style={{width: 168}} disabled/>
        </div>
        <div className="style-guide-element has-error">
          <Input placeholder="Label" style={{width: 168}}/>
        </div>
        <div className="style-guide-element">
          <Checkbox />
        </div>
        <div className="style-guide-element">
          <Checkbox>Check me</Checkbox>
        </div>
        <div className="style-guide-element">
          <Checkbox disabled>Check me</Checkbox>
        </div>
        <div className="style-guide-element">
          <Radio>Radio</Radio>
        </div>
        <div className="style-guide-element">
          <Radio disabled>Radio</Radio>
        </div>
        <div className="style-guide-element">
          <Select defaultValue="lucy" style={{width: 120}}>
            <Select.Option value="jack">Jack</Select.Option>
            <Select.Option value="lucy">Lucy</Select.Option>
            <Select.Option value="disabled" disabled>Disabled</Select.Option>
            <Select.Option value="Yiminghe">yiminghe</Select.Option>
          </Select>
        </div>
        <div className="style-guide-element">
          <Select defaultValue="lucy" style={{width: 120}} disabled>
            <Select.Option value="jack">Jack</Select.Option>
            <Select.Option value="lucy">Lucy</Select.Option>
            <Select.Option value="disabled" disabled>Disabled</Select.Option>
            <Select.Option value="Yiminghe">yiminghe</Select.Option>
          </Select>
        </div>
        <div className="style-guide-element">
          <a href="javascript:void(0);">Link</a>
        </div>
        <div className="style-guide-element">
          <h3>Form</h3>
          <ui.Form form="style-guide">
            <ui.Form.Item offset="normal">
              <ui.Form.Input name="something" validate={[Validation.Rules.required]}/>
            </ui.Form.Item>
            <ui.Form.Item label="Name" offset="normal">
              <ui.Form.Input name="name" placeholder="Name" validate={[Validation.Rules.required]}/>
            </ui.Form.Item>
            <ui.Form.Item label="Email" offset="normal">
              <ui.Form.Input name="email" placeholder="Email" validate={[Validation.Rules.required]}/>
            </ui.Form.Item>
            <ui.Form.Item>
              <Button size="default">Login</Button>
            </ui.Form.Item>
          </ui.Form>
        </div>
        <div className="style-guide-element">
          <h3>Inline form</h3>
          <ui.Form layout="inline" form="style-guide">
            <ui.Form.Item label="Name" offset="normal">
              <ui.Form.Input name="name" placeholder="Name" validate={[Validation.Rules.required]}/>
            </ui.Form.Item>
            <ui.Form.Item label="Email" offset="normal">
              <ui.Form.Input name="email" placeholder="Email" validate={[Validation.Rules.required]}/>
            </ui.Form.Item>
            <ui.Form.Item position="center" offset="normal">
              <Button size="default">Login</Button>
            </ui.Form.Item>
          </ui.Form>
        </div>
        <div className="style-guide-element">
          <h3>Group form</h3>
          <ui.Form layout="inline" form="style-guide">
            <ui.Form.ItemsGroup>
              <ui.Form.Item label="Name" offset="normal">
                <ui.Form.Input name="name" placeholder="Name" validate={[Validation.Rules.required]}/>
              </ui.Form.Item>
              <ui.Form.Item label="Email" offset="normal">
                <ui.Form.Input icon="user" name="email" placeholder="Email" validate={[Validation.Rules.required]}/>
              </ui.Form.Item>
              <ui.Form.Item position="center" offset="normal">
                <Button size="default">Login</Button>
              </ui.Form.Item>
            </ui.Form.ItemsGroup>
          </ui.Form>
        </div>
      </div>
    );
  }
}

export default StyleGuide;
