import React from 'react';
import { MainLayout } from 'components';
import { Button } from 'antd';
import '../styless.less';

class Index extends React.Component {

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Rules Engine"
                           options={(
                             <div>
                               <Button type="primary">Update Rules</Button>
                             </div>

                           )}
        />
        <MainLayout.Content
          className="layout-content-rules-engine-text-area product-edit-content">
          <textarea className="rules-engine-text-area"></textarea>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default Index;
